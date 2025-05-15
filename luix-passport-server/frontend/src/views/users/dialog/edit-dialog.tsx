import { useState, useEffect, ReactNode } from 'react'
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import CheckboxFormField from "@/components/custom/form-field/checkbox"
import { type CheckboxOption } from "@/components/custom/form-field/checkbox"
import SwitchFormField from "@/components/custom/form-field/switch"
import SelectFormField from "@/components/custom/form-field/select"
import PhoneInputFormField from "@/components/custom/form-field/phone-input"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { type User, userSchema, initialUserState } from "@/domains/user"
import { type DataDict } from "@/domains/data-dict"
import { Separator } from "@/components/ui/separator"
import { locales } from "@/data/locales"
import { DataDictService } from "@/services/data-dict-service"
import { AccountService } from "@/services/account-service"
import { UserService } from "@/services/user-service"
import { merge } from "@/lib/utils"
import type { Option } from "@/components/custom/form-field/combo-box"
import type { SupportedTimezone } from "@/domains/supported-timezone"
import type { SupportedDateTimeFormat } from "@/domains/supported-date-time-format"

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
  const [enabledRoles, setEnabledRoles] = useState(Array<CheckboxOption>)
  const [supportedTimezones, setSupportedTimezones] = useState(Array<Option>)
  const [supportedDateTimeFormats, setSupportedDateTimeFormats] = useState(Array<Option>)
  const form = useForm<User>({
    resolver: zodResolver(userSchema),
    defaultValues: initialUserState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    Promise.all([
      DataDictService.lookup("role", true),
      AccountService.findSupportedTimezones(),
      AccountService.findSupportedDateTimeFormats()
    ]).then(results => {
      // load options
      setEnabledRoles(results[0].data.map((item: DataDict) => ({label: item.dictCode, value: item.dictCode})));
      setSupportedTimezones(results[1].data.map((item: SupportedTimezone) =>
        ({label: item.displayName + " (UTC" + item.utcOffset + ")", value: item.id})));
      setSupportedDateTimeFormats(results[2].data.map((item: SupportedDateTimeFormat) =>
        ({label: item.displayName + " (" + item.example + ")", value: item.id})));

      id && UserService.findById(id).then(r => {
        form.reset(merge(r.data, initialUserState))
      })
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
            name="timeZoneId"
            label="Time Zone"
            options={supportedTimezones}
            formItemClassName="w-full"
            required
          />

          <SelectFormField 
            control={form.control} 
            name="dateTimeFormatId"
            label="Date Time Format"
            options={supportedDateTimeFormats}
            formItemClassName="w-full"
            required
          />
        </div>

        <InputFormField 
          control={form.control} 
          name="remark" 
          label="Remark"
        />

        <CheckboxFormField
          control={form.control}
          name="roles"
          label="Roles"
          required
          options={enabledRoles}
          requiredOptionValues={["ROLE_ANONYMOUS", "ROLE_USER"]}
          description="Select multiple roles for this user"
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
