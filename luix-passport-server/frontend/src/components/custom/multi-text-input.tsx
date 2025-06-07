import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { IconMinus, IconPlus } from "@tabler/icons-react"
import { useFieldArray, useFormContext } from "react-hook-form";
import { FormDescription } from "@/components/ui/form";
import { RequiredFormLabel } from "@/components/custom/required-form-label"

interface MultiTextInputProps {
  name: string;
  label?: string;
  description?: string;
  placeholder?: string;
  addButtonText?: string;
  inputClassName?: string;
  required?: boolean
}

export function MultiTextInput({
                                 name,
                                 label,
                                 description,
                                 placeholder = "Enter text...",
                                 addButtonText = "Add",
                                 inputClassName,
                                 required
                               }: MultiTextInputProps) {
  const { control } = useFormContext();
  const { fields, append, remove } = useFieldArray({
    control,
    name,
  });

  return (
    <div className="space-y-2">
      {label && (
        <div className="space-y-1">
          <RequiredFormLabel required={required}>{label}</RequiredFormLabel>
          {description && (
            <FormDescription>
              {description}
            </FormDescription>
          )}
        </div>
      )}

      <div className="space-y-3">
        {fields.map((field, index) => (
          <div key={field.id} className="flex items-center gap-2">
            <Input
              className={inputClassName}
              placeholder={placeholder}
              {...control.register(`${name}.${index}`)}
            />
            <Button
              type="button"
              variant="outline"
              size="icon"
              onClick={() => remove(index)}
            >
              <IconMinus className="size-4" />
            </Button>
          </div>
        ))}
      </div>

      <Button
        type="button"
        variant="outline"
        className="mt-1"
        onClick={() => append("")}
      >
        <IconPlus className="size-4" />
        {addButtonText}
      </Button>
    </div>
  );
}