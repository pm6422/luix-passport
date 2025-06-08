import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectFormField from "@/components/custom/form-field/multi-select"
import { Option } from "@/components/custom/multi-select"
import SwitchFormField from "@/components/custom/form-field/switch"
import { type Auth2Client, auth2ClientSchema, initialAuth2ClientState } from "@/domains/auth2-client"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { MultiTextInput } from "@/components/custom/multi-text-input"

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
    if (id) {
      Oauth2ClientService.findById(id).then(r => {
        form.reset(r.data)
      })
    }
  }, [form, id, open])

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

        <MultiTextInput
          name="redirectUris"
          label="Redirect URIs"
          required
          placeholder="Enter an URI"
          description="Valid redirect URIs after login successfully."
          addButtonText="Add URI"
        />

        <MultiTextInput
          name="postLogoutRedirectUris"
          label="Post Logout Redirect URIs"
          required
          placeholder="Enter an URI"
          addButtonText="Add URI"
        />

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
          description="Prevent login accesswhen disabled."
        />
      </SaveDialogContent>
    </Dialog>
  )
}
