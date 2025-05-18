import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import MultiSelect from "@/components/custom/multi-select"
import { Option } from "@/components/custom/multi-select"
import { Key } from "react"


interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  options: Array<Option>
  creatable?: boolean
  multiple?: boolean
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  multiSelectClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const MultiSelectFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  key,
  label,
  options,
  creatable,
  multiple,
  description,
  placeholder,
  required,
  disabled,
  multiSelectClassName,
  formItemClassName,
  hide = false
}: Props<TFieldValues>) => ( !hide &&
  <FormField
    control={disabled ? undefined : control}
    name={name as Path<TFieldValues>}
    key={key}
    render={({ field }) => (
      <FormItem className={formItemClassName}>
        {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
        <FormControl>
          <MultiSelect
            options={options}
            defaultValue={field.value}
            onValueChange={field.onChange}
            creatable={creatable}
            multiple={multiple}
            placeholder={placeholder}
            className={multiSelectClassName}
          />
        </FormControl>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default MultiSelectFormField
