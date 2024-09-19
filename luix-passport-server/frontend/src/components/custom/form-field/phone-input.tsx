import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import { PhoneInput } from "@/components/custom/phone-input"
import { CountryCode } from "libphonenumber-js"
import { Key } from "react"

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  defaultCountry?: CountryCode
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  phoneInputClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const PhoneInputFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  key,
  label,
  defaultCountry = "CN",
  description,
  placeholder,
  required,
  disabled,
  phoneInputClassName,
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
          <PhoneInput defaultCountry={defaultCountry} international placeholder={placeholder} className={phoneInputClassName} {...field} />
        </FormControl>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default PhoneInputFormField
