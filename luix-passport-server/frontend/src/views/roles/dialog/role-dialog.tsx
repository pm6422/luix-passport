import { ReactNode } from "react"
import { FormDialog } from "@/components/custom/dialog/form-dialog"
import { Option } from "@/components/custom/form-field/multi-selector"
import { type Role, roleSchema, initialRoleState } from "@/domains/role"
import { type Permission } from "@/domains/permission"
import { RoleService } from "@/services/role-service"
import { PermissionService } from "@/services/permission-service"
import { RoleFormFields } from "./role-form-fields"

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
  const loadRole = async (entityId?: string | null) => {
    const { data: permissionList } = await PermissionService.find({ page: 0, size: 2000, sort: ["updatedAt,desc"] })
    const options = permissionList.map((item: Permission) => ({ label: item.id, value: item.id }))
    if (entityId) {
      const { data } = await RoleService.findById(entityId)
      return { options: { permissions: options }, entity: data }
    }
    return { options: { permissions: options }, entity: initialRoleState }
  }
  return (
    <FormDialog<Role, { permissions: Option[] }>
      entityName={entityName}
      id={id}
      schema={roleSchema}
      defaultValues={initialRoleState}
      save={save}
      onLoad={loadRole}
      renderFields={(form, options) => (
        <RoleFormFields form={form} permissions={options.permissions} />
      )}
    >
      {children}
    </FormDialog>
  )
}
