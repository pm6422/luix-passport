import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import { Key } from "react"
import { ClearableSelect } from "@/components/custom/clearable-select"

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  options: { 
    label: string 
    value: string 
  }[]
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  selectContentClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const ClearableSelectFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  key,
  label,
  options,
  description,
  placeholder,
  required,
  disabled,
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
          <ClearableSelect options={options} defaultValue={field.value} onValueChange={field.onChange} placeholder={placeholder}/>
        </FormControl>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default ClearableSelectFormField
