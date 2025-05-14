import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Key } from "react"

export type RadioOption = {
  label: string
  value: string
}

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  key?: Key
  label?: string
  options: Array<RadioOption>
  description?: string
  required?: boolean
  disabled?: boolean
  formItemClassName?: string
  hide?: boolean
  direction?: "vertical" | "horizontal"
}

const RadioGroupFormField = <TFieldValues extends FieldValues>({
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
                                                                 direction = "vertical"
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
          <FormControl>
            <RadioGroup
              onValueChange={field.onChange}
              defaultValue={field.value}
              disabled={disabled}
              className={`flex ${direction === "vertical" ? "flex-col space-y-1" : "flex-row space-x-4"}`}
            >
              {options.map((item) => (
                <FormItem key={item.value} className="flex items-center space-x-3 space-y-0">
                  <FormControl>
                    <RadioGroupItem value={item.value} />
                  </FormControl>
                  <FormLabel className="font-normal">
                    {item.label}
                  </FormLabel>
                </FormItem>
              ))}
            </RadioGroup>
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default RadioGroupFormField