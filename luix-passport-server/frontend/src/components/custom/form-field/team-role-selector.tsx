import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import { TeamRoleSelector } from "../team-role-selector"

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: Path<TFieldValues>
  label: string
  allRoles: string[]
  required?: boolean
  disabled?: boolean
  description?: string
  className?: string
}

const TeamRoleSelectorFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  label,
  allRoles,
  required,
  disabled,
  description,
  className
}: Props<TFieldValues>) => {
  return (
    <FormField
      control={control}
      name={name}
      render={({ field }) => (
        <FormItem className={className}>
          <RequiredFormLabel required={required}>{label}</RequiredFormLabel>
          <FormControl>
            <TeamRoleSelector
              value={field.value || []}
              onChange={field.onChange}
              allRoles={allRoles}
              disabled={disabled}
              className="w-full"
            />
          </FormControl>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default TeamRoleSelectorFormField
