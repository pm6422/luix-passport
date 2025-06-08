import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue
} from "@/components/ui/select"
import { Key } from "react"

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  options: { 
    label: string 
    value: string 
    icon?: React.ComponentType<{ className?: string }> 
  }[]
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  inputClassName?: string
  selectContentClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const SelectFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  key,
  label,
  options,
  description,
  placeholder,
  required,
  disabled,
  inputClassName,
  selectContentClassName,
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
        <Select onValueChange={field.onChange} value={field.value}>
          <FormControl className={inputClassName}>
            <SelectTrigger>
              <SelectValue placeholder={placeholder}/>
            </SelectTrigger>
          </FormControl>
          <SelectContent className={selectContentClassName}>
            {options.map(option => (
              <SelectItem key={option.value} value={option.value}>{option.label}</SelectItem>
            ))}
          </SelectContent>
        </Select>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default SelectFormField