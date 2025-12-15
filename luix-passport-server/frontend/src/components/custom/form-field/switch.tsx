import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "@/components/custom/form-element/required-form-label"
import { Switch } from "@/components/ui/switch"


interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  label?: string
  description?: string
  required?: boolean
  disabled?: boolean
  switchClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const SwitchFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  label,
  description,
  required,
  disabled,
  switchClassName,
  formItemClassName,
  hide = false
}: Props<TFieldValues>) => {
  if (hide) return null
  return (
    <FormField
      control={disabled ? undefined : control}
      name={name}
      render={({ field }) => (
        <FormItem className={formItemClassName}>
          {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
          <div className="flex items-center gap-2">
            <FormControl>
              <Switch
                checked={field.value}
                onCheckedChange={field.onChange}
                className={switchClassName}
                disabled={disabled}
                aria-readonly
              />
            </FormControl>
            {description && <FormDescription>{description}</FormDescription>}
          </div>
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default SwitchFormField
