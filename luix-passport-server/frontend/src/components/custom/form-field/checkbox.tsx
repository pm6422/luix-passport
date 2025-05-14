import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import { Checkbox } from "@/components/ui/checkbox"
import { Key } from "react"

export type Option = {
  label: string
  value: string
}

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  key?: Key
  label?: string
  options: Array<Option>
  description?: string
  required?: boolean
  disabled?: boolean
  formItemClassName?: string
  hide?: boolean
  requiredOptionValues?: string[] // Add this if you want to enforce certain values
}

const CheckboxFormField = <TFieldValues extends FieldValues>({
                                                               control,
                                                               name,
                                                               key,
                                                               label,
                                                               options,
                                                               description,
                                                               required,
                                                               disabled,
                                                               formItemClassName,
                                                               hide = false,
                                                               requiredOptionValues = []
                                                             }: Props<TFieldValues>) => {
  return !hide && (
    <FormField
      control={disabled ? undefined : control}
      name={name}
      key={key}
      render={({ field }) => (
        <FormItem className={formItemClassName}>
          {label && (
            <div>
              <RequiredFormLabel required={required}>{label}</RequiredFormLabel>
              {description && <FormDescription>{description}</FormDescription>}
            </div>
          )}

          {options.map((item) => (
            <FormItem
              key={item.value}
              className="flex flex-row items-start space-x-3 space-y-0"
            >
              <FormControl>
                <Checkbox
                  checked={field.value?.includes(item.value)}
                  disabled={disabled || requiredOptionValues.includes(item.value)}
                  onCheckedChange={(checked) => {
                    return checked
                      ? field.onChange([...field.value, item.value])
                      : field.onChange(
                        field.value?.filter(
                          (value: string) => value !== item.value
                        ) || []
                      )
                  }}
                />
              </FormControl>
              <FormLabel className="font-normal">
                {item.label}
                {requiredOptionValues.includes(item.value) && (
                  <span className="ml-1 text-destructive">*</span>
                )}
              </FormLabel>
            </FormItem>
          ))}
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default CheckboxFormField