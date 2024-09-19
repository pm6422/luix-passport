import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import Combobox from "@/components/custom/combobox"
import { Key } from "react"

export type Option = {
  label: string
  value: string
  icon?: React.ComponentType<{ className?: string }>
}

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
  comboboxClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const ComboboxFormField = <TFieldValues extends FieldValues>({
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
  comboboxClassName,
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
          <Combobox
            options={options}
            defaultValue={field.value}
            onValueChange={field.onChange}
            creatable={creatable}
            multiple={multiple}
            placeholder={placeholder}
            className={comboboxClassName}
          />
        </FormControl>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default ComboboxFormField
