import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import * as React from "react"
import { Check, ChevronsUpDown } from "lucide-react"
import { Key } from "react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"

export type Option = {
  label: string
  value: string
  icon?: React.ComponentType<{ className?: string }>
}

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  options: Array<Option>
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  formItemClassName?: string
  hide?: boolean
}

const ComboboxFormField = <TFieldValues extends FieldValues>({
  control,
  name,
  key,
  label,
  options,
  description,
  placeholder,
  required,
  disabled,
  formItemClassName,
  hide = false
}: Props<TFieldValues>) => {
  const [open, setOpen] = React.useState(false)
  const [value, setValue] = React.useState("")

  return ( !hide &&
    <FormField
      control={disabled ? undefined : control}
      name={name as Path<TFieldValues>}
      key={key}
      render={({ field }) => (
        <FormItem className={formItemClassName}>
          {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
            <Popover open={open} onOpenChange={setOpen}>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  role="combobox"
                  aria-expanded={open}
                  className="w-[200px] justify-between"
                >
                  {value
                    ? options.find((option) => option.value === field.value)?.label
                    : "Select ..."}
                  <ChevronsUpDown className="opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-[200px] p-0">
                <FormControl>
                  <Command>
                    <CommandInput placeholder={placeholder} className="h-9" />
                    <CommandList>
                      <CommandEmpty>No data found.</CommandEmpty>
                      <CommandGroup>
                        {options.map((option) => (
                          <CommandItem
                            key={option.value}
                            value={option.value}
                            onSelect={(currentValue) => {
                              setValue(currentValue === value ? "" : currentValue)
                              setOpen(false)
                            }}
                          >
                            {option.label}
                            <Check
                              className={cn(
                                "ml-auto",
                                value === option.value ? "opacity-100" : "opacity-0"
                              )}
                            />
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </CommandList>
                  </Command>
                </FormControl>
              </PopoverContent>
            </Popover>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  )
}

export default ComboboxFormField
