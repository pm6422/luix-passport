import { UseFormReturn } from "react-hook-form"
import InputFormField from "@/components/custom/form-field/input"
import MultiSelectorFormField, { Option } from "@/components/custom/form-field/multi-selector"
import { type Role } from "@/domains/role"

interface RoleFormFieldsProps {
  form: UseFormReturn<Role>
  permissions: Option[]
}

export function RoleFormFields({
  form,
  permissions
}: RoleFormFieldsProps) {
  return (
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
  )
}

