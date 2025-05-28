import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, useFieldArray } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectFormField from "@/components/custom/form-field/multi-select"
import { Option } from "@/components/custom/multi-select"
import { Button } from "@/components/custom/button"
import { IconX, IconCirclePlus } from "@tabler/icons-react"
import { FormLabel, FormDescription } from "@/components/ui/form"
import { RequiredFormLabel } from "@/components/custom/required-form-label"
import SwitchFormField from "@/components/custom/form-field/switch"
import { type Auth2Client, auth2ClientSchema, initialAuth2ClientState } from "@/domains/auth2-client"
import { Oauth2ClientService } from "@/services/oauth2-client-service"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Auth2Client) => Promise<void>,
  afterSave?: (success: boolean) => void
}

export function EditDialog({
  children,
  entityName,
  id,
  save,
  afterSave
}: EditDialogProps) {
  const [open, setOpen] = useState(false)
  const [authenticationMethodOptions, setAuthenticationMethodOptions] = useState(Array<Option>)
  const [grantTypeOptions, setGrantTypeOptions] = useState(Array<Option>)
  const [scopeOptions, setScopeOptions] = useState(Array<Option>)
  const form = useForm<Auth2Client>({
    resolver: zodResolver(auth2ClientSchema),
    defaultValues: initialAuth2ClientState
  })

  const { 
    fields: redirectUriFields, 
    append: addRedirectUri, 
    remove: removeRedirectUri
  } = useFieldArray({
    // @ts-ignore
    name: "redirectUris",
    control: form.control,
  })

  const { 
    fields: postLogoutRedirectUriFields, 
    append: addPostLogoutRedirectUri, 
    remove: removePostLogoutRedirectUri
  } = useFieldArray({
    // @ts-ignore
    name: "postLogoutRedirectUris",
    control: form.control,
  })

  useEffect(() => {
    if (!open) {
      return
    }
    Oauth2ClientService.findClientAuthenticationMethods().then(function (res) {
      const options = res.data.map((item: string) => ({ label: item, value: item }))
      setAuthenticationMethodOptions(options)
    })
    Oauth2ClientService.findAuthorizationGrantTypes().then(function (res) {
      const options = res.data.map((item: string) => ({ label: item, value: item }))
      setGrantTypeOptions(options)
    })
    Oauth2ClientService.findScopes().then(function (res) {
      const options = res.data.map((item: string) => ({ label: item, value: item }))
      setScopeOptions(options)
    })
    id && Oauth2ClientService.findById(id).then(r => {
      form.reset(r.data)
    })
  }, [open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen} debug={false}>
        <InputFormField 
          control={form.control} 
          name="clientId" 
          label="Client ID" 
          required 
          disabled={!!id}
        />

        <InputFormField 
          control={form.control} 
          name="clientName" 
          label="Client Name" 
          required
        />

        <InputFormField 
          control={form.control} 
          name="rawClientSecret" 
          label="Raw Client Secret" 
          required 
          description="Do not forget the secret." 
          hide={!!id}
        />

        <MultiSelectFormField
          control={form.control} 
          name="clientAuthenticationMethods"
          label="Authentication Methods"
          required
          options={authenticationMethodOptions}
          multiple={true}
        />

        <MultiSelectFormField
          control={form.control} 
          name="authorizationGrantTypes"
          label="Authentication Grant Types"
          required
          options={grantTypeOptions}
          multiple={true}
        />

        <div>
          <RequiredFormLabel>
            Redirect URIs
          </RequiredFormLabel>
          <FormDescription className="mb-3">
            Valid redirect URIs after login successfully.
          </FormDescription>
          {redirectUriFields.map((field, index) => (
            <InputFormField 
              control={form.control} 
              key={field.id}
              // @ts-ignore
              name={`redirectUris.${index}`}
              formItemClassName="mt-2"
              icon={
                <Button 
                  type="button"
                  variant="outline" 
                  className="flex size-9 p-0" 
                  onClick={() => removeRedirectUri(index)}>
                    <IconX className="size-4" />
                    <span className="sr-only">Delete</span>
                </Button>
              }
            />
          ))}
          <div className="flex items-center justify-end w-full mt-2">
            <IconCirclePlus
              className="size-6 mt-1 mr-1 cursor-pointer text-muted-foreground"
              type="button"
              onClick={() => addRedirectUri("")}
            />
          </div>
        </div>
        <div>
          <FormLabel className="mb-3">
            Post Logout Redirect URIs
          </FormLabel>
          {postLogoutRedirectUriFields.map((field, index) => (
            <InputFormField 
              control={form.control} 
              key={field.id}
              // @ts-ignore
              name={`postLogoutRedirectUris.${index}`}
              formItemClassName="mt-2"
              icon={
                <Button 
                  type="button"
                  variant="outline" 
                  className="flex size-9 p-0" 
                  onClick={() => removePostLogoutRedirectUri(index)}>
                    <IconX className="size-4" />
                    <span className="sr-only">Delete</span>
                </Button>
              }
            />
          ))}
          <div className="flex items-center justify-end w-full mt-2">
            <IconCirclePlus
              className="size-6 mt-1 mr-1 cursor-pointer text-muted-foreground"
              type="button"
              onClick={() => addPostLogoutRedirectUri("")}
            />
          </div>
        </div>

        <MultiSelectFormField
          control={form.control} 
          name="scopes"
          label="Scopes"
          required
          options={scopeOptions}
          multiple={true}
        />

        <SwitchFormField 
          control={form.control} 
          name="enabled" 
          label="Enabled"
          description="After disabling, existing data can still reference the object, but new data cannot."
        />
      </SaveDialogContent>
    </Dialog>
  )
}
