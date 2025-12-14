import { useState, ReactNode } from "react"
import { SaveDialog } from "@/components/custom/dialog/save-dialog"
// Form & Custom Components
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectorFormField, { Option } from "@/components/custom/form-field/multi-selector"
// Domain
import { type Role, roleSchema, initialRoleState } from "@/domains/role"
import { type Permission } from "@/domains/permission"
import { RoleService } from "@/services/role-service"
import { PermissionService } from "@/services/permission-service"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Role) => Promise<void>
}

export function EditDialog({
  children,
  entityName,
  id,
  save
}: EditDialogProps) {
  const [permissions, setPermissions] = useState<Option[]>([])
  return (
    <SaveDialog<Role, { permissions: Option[] }>
      entityName={entityName}
      id={id}
      schema={roleSchema}
      defaultValues={initialRoleState}
      save={save}
      onLoad={async (entityId) => {
        const { data: permissionList } = await PermissionService.find({ page: 0, size: 2000, sort: ["updatedAt,desc"] })
        const options = permissionList.map((item: Permission) => ({ label: item.id, value: item.id }))
        setPermissions(options)
        if (entityId) {
          const { data } = await RoleService.findById(entityId)
          return { options: { permissions: options }, entity: data }
        }
        return { options: { permissions: options }, entity: initialRoleState }
      }}
      renderFields={(form) => (
        <>
          <InputFormField
            control={form.control}
            name="id"
            label="ID"
            required
          />
          <InputFormField
            control={form.control}
            name="remark"
            label="Remark"
          />
          <MultiSelectorFormField
            control={form.control}
            name="permissionIds"
            label="Permissions"
            options={permissions}
            multiple={true}
            required
          />
        </>
      )}
    >
      {children}
    </SaveDialog>
  )
}
