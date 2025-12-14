import { UseFormReturn } from "react-hook-form"
import InputFormField from "@/components/custom/form-field/input"
import SelectFormField from "@/components/custom/form-field/select"
import { Option } from "@/components/custom/form-field/multi-selector"
import { type Permission } from "@/domains/permission"

interface PermissionFormFieldsProps {
  form: UseFormReturn<Permission>
  resourceTypes: Option[]
  actions: Option[]
}

export function PermissionFormFields({
  form,
  resourceTypes,
  actions
}: PermissionFormFieldsProps) {
  return (
    <>
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
        options={resourceTypes}
        formItemClassName="w-full"
        required
      />

      <SelectFormField
        control={form.control}
        name="action"
        label="Action"
        options={actions}
        formItemClassName="w-full"
        required
      />

      <InputFormField
        control={form.control}
        name="remark"
        label="Remark"
      />
    </>
  )
}
