import { useState, useEffect, ReactNode } from 'react'
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import { type CheckboxOption } from "@/components/custom/form-field/checkbox"
import SelectFormField from "@/components/custom/form-field/select"
import { type Permission, permissionSchema, initialPermissionState } from "@/domains/permission"
import { locales } from "@/data/locales"
import { AccountService } from "@/services/account-service"
import { PermissionService } from "@/services/permission-service"
import { RoleService } from "@/services/role-service"
import { Option } from "@/components/custom/multi-select"
import type { SupportedTimezone } from "@/domains/supported-timezone"
import type { SupportedDateTimeFormat } from "@/domains/supported-date-time-format"
import { type Role } from "@/domains/role"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Permission) => Promise<void>,
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
  const form = useForm<Permission>({
    resolver: zodResolver(permissionSchema),
    defaultValues: initialPermissionState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    Promise.all([
      RoleService.findAll(),
      AccountService.findSupportedTimezones(),
      AccountService.findSupportedDateTimeFormats()
    ]).then(results => {
      // load options
      setEnabledRoles(results[0].data.map((item: Role) => ({label: item.id || "", value: item.id || ""})));
      setSupportedTimezones(results[1].data.map((item: SupportedTimezone) =>
        ({label: "(UTC" + item.utcOffset + ") " + item.id , value: item.id})));
      setSupportedDateTimeFormats(results[2].data.map((item: SupportedDateTimeFormat) =>
        ({label: item.displayName + " (" + item.example + ")", value: item.id})));

      id && PermissionService.findById(id).then(r => {
        form.reset(r.data)
      })
    })
  }, [open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen}>

        <InputFormField
          control={form.control}
          name="id"
          label="ID"
          required
        />

        <SelectFormField
          control={form.control}
          name="resourceType"
          label="Resource Type"
          options={locales}
          formItemClassName="w-full"
          required
        />


      </SaveDialogContent>
    </Dialog>
  )
}