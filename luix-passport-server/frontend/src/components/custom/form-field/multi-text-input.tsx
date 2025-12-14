import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { IconMinus, IconPlus } from "@tabler/icons-react"
import { useFieldArray } from "react-hook-form";
import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form";
import { RequiredFormLabel } from "../required-form-label"

// ----- MultiTextInput Component -----

interface MultiTextInputProps<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  placeholder?: string
  addButtonText?: string
  inputClassName?: string
  disabled?: boolean
}

function MultiTextInput<TFieldValues extends FieldValues>({
  control,
  name,
  placeholder = "Enter text...",
  addButtonText = "Add",
  inputClassName,
  disabled
}: MultiTextInputProps<TFieldValues>) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: name as any
  });

  return (
    <div className="space-y-3">
      {fields.map((field, index) => (
        <div key={field.id} className="flex items-center gap-2">
          <Input
            className={inputClassName}
            placeholder={placeholder}
            disabled={disabled}
            {...control.register(`${name}.${index}` as any)}
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
        onClick={() => append("" as any)}
      >
        <IconPlus className="size-4" />
        {addButtonText}
      </Button>
    </div>
  );
}

// ----- Wrapper -----

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  label?: string
  description?: string
  placeholder?: string
  addButtonText?: string
  inputClassName?: string
  required?: boolean
  disabled?: boolean
  formItemClassName?: string
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
  formItemClassName,
  hide = false
}: Props<TFieldValues>) => {
  if (hide) return null

  return (
    <FormField
      control={control}
      name={name}
      render={() => (
        <FormItem className={formItemClassName}>
          {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
          <FormControl>
            <MultiTextInput
              control={control}
              name={name}
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
