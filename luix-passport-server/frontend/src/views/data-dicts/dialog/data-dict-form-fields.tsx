import { UseFormReturn } from "react-hook-form"
import MultiSelectorFormField, { Option } from "@/components/custom/form-field/multi-selector"
import InputFormField from "@/components/custom/form-field/input"
import SwitchFormField from "@/components/custom/form-field/switch"
import { type DataDict } from "@/domains/data-dict"

interface Props {
  form: UseFormReturn<DataDict>
  categoryCodeOptions: Option[]
}

export function DataDictFormFields({ form, categoryCodeOptions }: Props) {
  return (
    <>
      <MultiSelectorFormField
        control={form.control}
        name="categoryCode"
        label="Category Code"
        options={categoryCodeOptions}
        placeholder="Select or input a category code"
        creatable={true}
        required
      />
      <InputFormField
        control={form.control}
        name="dictCode"
        label="Dictionary Code"
        required
      />
      <InputFormField
        control={form.control}
        name="dictName"
        label="Dictionary Name"
      />
      <InputFormField
        control={form.control}
        name="remark"
        label="Remark"
      />
      <SwitchFormField
        control={form.control}
        name="enabled"
        label="Enabled"
        description="After disabling, existing data can still reference the object, but new data cannot."
      />
    </>
  )
}
