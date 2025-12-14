import type { ReactNode } from "react"
import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { RequiredFormLabel } from "@/components/custom/form-element/required-form-label"

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  label?: string
  value?: string
  defaultValue?: string
  type?: Parameters<typeof Input>[0]["type"] | "textarea"
  description?: string
  placeholder?: string
  onFocus?: () => void
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void
  subComponent?: React.ReactNode
  required?: boolean
  disabled?: boolean
  icon?: ReactNode
  inputClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const InputFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  label,
  value,
  defaultValue,
  description,
  onFocus,
  onKeyDown,
  type = "text",
  placeholder,
  subComponent,
  required,
  disabled,
  icon,
  inputClassName,
  formItemClassName,
  hide = false
}: Props<TFieldValues>) => {
  if (hide) return null
  return (
    <FormField
      control={disabled ? undefined : control}
      name={name}
      render={({ field: { value: formFieldValue, ...rest } }) => (
        <FormItem className={formItemClassName}>
          {label && <RequiredFormLabel
            htmlFor={name.toString()}
            required={required}>{label}
          </RequiredFormLabel>}
          <FormControl>
            <div className="relative flex w-full items-center gap-2">
              {type === "textarea" ? (
                <Textarea
                  id={name.toString()}
                  className={inputClassName}
                  placeholder={placeholder}
                  onFocus={onFocus}
                  defaultValue={defaultValue}
                  value={value || formFieldValue || ""}
                  disabled={disabled}
                  {...rest}
                />
              ) : (
                <Input
                  id={name.toString()}
                  className={inputClassName}
                  type={type}
                  onFocus={onFocus}
                  onKeyDown={onKeyDown}
                  placeholder={placeholder}
                  defaultValue={defaultValue}
                  value={value || formFieldValue || ""}
                  disabled={disabled}
                  {...rest}
                />
              )}
              {icon && (icon)}
              {subComponent}
            </div>
          </FormControl>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default InputFormField
