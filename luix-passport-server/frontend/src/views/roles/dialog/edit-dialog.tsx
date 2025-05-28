import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectFormField from "@/components/custom/form-field/multi-select"
import { Option } from "@/components/custom/multi-select"
import { type Role, roleSchema, initialRoleState } from "@/domains/role"
import { type Permission } from "@/domains/permission"
import { RoleService } from "@/services/role-service"
import { PermissionService } from "@/services/permission-service"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Role) => Promise<void>,
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
  const [permissions, setPermissions] = useState(Array<Option>)
  const form = useForm<Role>({
    resolver: zodResolver(roleSchema),
    defaultValues: initialRoleState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    Promise.all([
      PermissionService.find({ page: 0, size: 2000, sort: ["modifiedAt,desc"]}),
    ]).then(results => {
      // load options
      setPermissions(results[0].data.map((item: Permission) => ({label: item.id, value: item.id})));

      id && RoleService.findById(id).then(r => {
        form.reset(r.data)
      })
    })
  }, [form, id, open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen} debug={false}>
        <InputFormField
          control={form.control} 
          name="id"
          label="ID"
          required
        />

        <MultiSelectFormField
          control={form.control}
          name="permissionIds"
          label="Permissions"
          required
          options={permissions}
          placeholder="Select permissions"
        />

      </SaveDialogContent>
    </Dialog>
  )
}
