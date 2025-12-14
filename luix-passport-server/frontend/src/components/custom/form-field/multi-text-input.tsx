import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { IconMinus, IconPlus } from "@tabler/icons-react"
import type { Control, FieldValues, Path, ArrayPath, FieldArray } from "react-hook-form"
import { useFieldArray } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "@/components/custom/form-element/required-form-label"

// ----- MultiTextInput Component -----

export interface MultiTextInputProps<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: ArrayPath<TFieldValues>
  placeholder?: string
  addButtonText?: string
  inputClassName?: string
  disabled?: boolean
}

export function MultiTextInput<TFieldValues extends FieldValues>({
  control,
  name,
  placeholder = "Enter text...",
  addButtonText = "Add",
  inputClassName,
  disabled
}: MultiTextInputProps<TFieldValues>) {
  const { fields, append, remove } = useFieldArray({
    control,
    name
  });

  return (
    <div className="space-y-3">
      {fields.map((field, index) => (
        <div key={field.id} className="flex items-center gap-2">
          <Input
            className={inputClassName}
            placeholder={placeholder}
            disabled={disabled}
            {...control.register(`${name}.${index}` as Path<TFieldValues>)}
          />
          <Button
            type="button"
            variant="outline"
            size="icon"
            disabled={disabled}
            onClick={() => remove(index)}
          >
            <IconMinus className="size-4" />
          </Button>
        </div>
      ))}
      <Button
        type="button"
        variant="outline"
        className="mt-1"
        disabled={disabled}
        onClick={() => append("" as unknown as FieldArray<TFieldValues, ArrayPath<TFieldValues>>)}
      >
        <IconPlus className="size-4" />
        {addButtonText}
      </Button>
    </div>
  )
}

// ----- MultiTextInputFormField Component -----

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  label: string
  description?: string
  placeholder?: string
  addButtonText?: string
  inputClassName?: string
  required?: boolean
  disabled?: boolean
  className?: string
  hide?: boolean
}

const MultiTextInputFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  label,
  description,
  placeholder,
  addButtonText,
  inputClassName,
  required,
  disabled,
  className,
  hide = false
}: Props<TFieldValues>) => {
  if (hide) return null

  return (
    <FormField
      control={disabled ? undefined : control}
      name={name}
      render={() => (
        <FormItem className={className}>
          <RequiredFormLabel required={required}>{label}</RequiredFormLabel>
          <FormControl>
            <MultiTextInput
              control={control}
              name={name as ArrayPath<TFieldValues>}
              placeholder={placeholder}
              addButtonText={addButtonText}
              inputClassName={inputClassName}
              disabled={disabled}
            />
          </FormControl>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default MultiTextInputFormField
