import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import MultipleSelector, { Option } from '@/components/custom/multiple-selector'
import { Key } from "react"

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  options: Array<Option>
  defaultOptions?: Array<Option>
  creatable?: boolean
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  maxSelected?: number
  onMaxSelected?: (maxLimit: number) => void
  hidePlaceholderWhenSelected?: boolean
  groupBy?: string
  className?: string
  badgeClassName?: string
  hide?: boolean
  loadingIndicator?: React.ReactNode
  emptyIndicator?: React.ReactNode
  delay?: number
  onSearch?: (value: string) => Promise<Option[]>
  onSearchSync?: (value: string) => Option[]
  triggerSearchOnFocus?: boolean
  hideClearAllButton?: boolean
}

const MultipleSelectorFormField = <TFieldValues extends FieldValues>({
                                                                       control,
                                                                       name,
                                                                       key,
                                                                       label,
                                                                       options,
                                                                       defaultOptions,
                                                                       creatable,
                                                                       description,
                                                                       placeholder,
                                                                       required,
                                                                       disabled,
                                                                       maxSelected,
                                                                       onMaxSelected,
                                                                       hidePlaceholderWhenSelected,
                                                                       groupBy,
                                                                       className,
                                                                       badgeClassName,
                                                                       hide = false,
                                                                       loadingIndicator,
                                                                       emptyIndicator,
                                                                       delay,
                                                                       onSearch,
                                                                       onSearchSync,
                                                                       triggerSearchOnFocus,
                                                                       hideClearAllButton,
                                                                     }: Props<TFieldValues>) => (
  !hide &&
  <FormField
    control={disabled ? undefined : control}
    name={name as Path<TFieldValues>}
    key={key}
    render={({ field }) => (
      <FormItem className={className}>
        {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
        <FormControl>
          <MultipleSelector
            value={field.value}
            onChange={field.onChange}
            options={options}
            defaultOptions={defaultOptions}
            creatable={creatable}
            placeholder={placeholder}
            disabled={disabled}
            maxSelected={maxSelected}
            onMaxSelected={onMaxSelected}
            hidePlaceholderWhenSelected={hidePlaceholderWhenSelected}
            groupBy={groupBy}
            badgeClassName={badgeClassName}
            loadingIndicator={loadingIndicator}
            emptyIndicator={emptyIndicator}
            delay={delay}
            onSearch={onSearch}
            onSearchSync={onSearchSync}
            triggerSearchOnFocus={triggerSearchOnFocus}
            hideClearAllButton={hideClearAllButton}
          />
        </FormControl>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default MultipleSelectorFormField