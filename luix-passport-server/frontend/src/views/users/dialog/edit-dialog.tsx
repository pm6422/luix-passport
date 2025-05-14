import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import { type Option } from "@/components/custom/form-field/combobox"
import SwitchFormField from "@/components/custom/form-field/switch"
import SelectFormField from "@/components/custom/form-field/select"
import PhoneInputFormField from "@/components/custom/form-field/phone-input"
import { RequiredFormLabel } from "@/components/custom/required-form-label"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { type User, userSchema, initialUserState } from "@/domains/user.ts"
import { type DataDict } from "@/domains/data-dict"
import { Separator } from "@/components/ui/separator"
import { locales } from "@/data/locales"
import { timeZones } from "@/data/time-zones"
import { dateTimeFormats } from "@/data/date-time-formats"
import { DataDictService } from "@/services/data-dict-service"
import { UserService } from "@/services/user-service"
import { merge } from "@/lib/utils"
import { FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form.tsx'
import { Checkbox } from '@/components/ui/checkbox.tsx'

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string,
  save: (formData: User) => Promise<void>,
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
  const [enabledRoles, setEnabledRoles] = useState(Array<Option>)
  const form = useForm<User>({
    resolver: zodResolver(userSchema),
    defaultValues: initialUserState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    DataDictService.lookup("role", true).then(r => {
      setEnabledRoles(r.data.map((item: DataDict) => ({label: item.dictCode, value: item.dictCode})))
    })
    id && UserService.findById(id).then(r => {
      form.reset(merge(r.data, initialUserState))
    })
  }, [open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen}>
        <div className="flex items-center justify-between gap-5">
          <Avatar className="size-20">
            <AvatarImage src={"api/user-profile-pics/" + id} alt="profile" />
            <AvatarFallback>Avatar</AvatarFallback>
          </Avatar>
          <Separator orientation="vertical" />
          <InputFormField 
            control={form.control} 
            name="username" 
            label="Username"
            formItemClassName="w-full"
            required 
            disabled={!!id}
          />
        </div>

        <InputFormField 
          control={form.control} 
          name="email" 
          label="Email" 
          required
        />

        <PhoneInputFormField 
          control={form.control} 
          name="mobileNo" 
          label="Mobile No" 
          required
          placeholder="Enter a phone number"
        />
        
        <div className="flex items-center gap-2">
          <InputFormField 
            control={form.control} 
            name="firstName" 
            label="First Name" 
            formItemClassName="w-full"
          />
          
          <InputFormField 
            control={form.control} 
            name="lastName" 
            label="Last Name" 
            formItemClassName="w-full"
          />
        </div>

        <SelectFormField
          control={form.control}
          name="locale"
          label="Preferred Language"
          options={locales}
          formItemClassName="w-full"
          required
        />

        <div className="flex items-center gap-2">
          <SelectFormField 
            control={form.control} 
            name="timeZone" 
            label="Time Zone"
            options={timeZones}
            formItemClassName="w-full"
            required
          />

          <SelectFormField 
            control={form.control} 
            name="dateTimeFormat" 
            label="Date Time Format"
            options={dateTimeFormats}
            formItemClassName="w-full"
            required
          />
        </div>

        <InputFormField 
          control={form.control} 
          name="remark" 
          label="Remark"
        />

        <FormField
          control={form.control}
          name="roles"
          render={() => (
            <FormItem>
              <div className="mb-4">
                <RequiredFormLabel required={true}>Roles</RequiredFormLabel>
                <FormDescription>
                  Select the user roles, ROLE_ANONYMOUS and ROLE_USER are required for each user.
                </FormDescription>
              </div>
              {enabledRoles.map((item) => (
                <FormField
                  key={item.value}
                  control={form.control}
                  name="roles"
                  render={({ field }) => {
                    return (
                      <FormItem
                        key={item.value}
                        className="flex flex-row items-start space-x-3 space-y-0"
                      >
                        <FormControl>
                          <Checkbox
                            checked={field.value?.includes(item.value)}
                            onCheckedChange={(checked) => {
                              return checked
                                ? field.onChange([...field.value, item.value])
                                : field.onChange(
                                  field.value?.filter(
                                    (value) => value !== item.value
                                  )
                                )
                            }}
                          />
                        </FormControl>
                        <FormLabel className="font-normal">
                          {item.label}
                        </FormLabel>
                      </FormItem>
                    )
                  }}
                />
              ))}
              <FormMessage />
            </FormItem>
          )}
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
