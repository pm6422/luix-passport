import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import * as React from "react"
import { IconCheck, IconSelector } from "@tabler/icons-react"
import { Key } from "react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Option } from "@/components/custom/multi-select"
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
  inputClassName?: string
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
                                                               inputClassName,
                                                               formItemClassName,
                                                               hide = false
                                                             }: Props<TFieldValues>) => {
  const [open, setOpen] = React.useState(false)

  return ( !hide &&
    <FormField
      control={disabled ? undefined : control}
      name={name as Path<TFieldValues>}
      key={key}
      render={({ field }) => (
        <FormItem className={formItemClassName}>
          {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
          <Popover open={open} onOpenChange={setOpen}>
            <FormControl>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  role="combobox"
                  aria-expanded={open}
                  className={cn(
                    "w-full justify-between font-normal",
                    inputClassName
                  )}
                >
                  {field.value
                    ? options.find((option) => option.value === field.value)?.label
                    : placeholder || "Select..."}
                  <IconSelector className="size-4 opacity-50" />
                </Button>
              </PopoverTrigger>
            </FormControl>
            <PopoverContent
              className="min-w-fit p-0 drop-shadow-xs"
              align="start"
            >
              <Command>
                <CommandInput placeholder={placeholder} className="h-9" />
                <CommandList>
                  <CommandEmpty>No option found.</CommandEmpty>
                  <CommandGroup>
                    {options.map((option) => (
                      <CommandItem
                        key={option.value}
                        value={option.value}
                        onSelect={() => {
                          field.onChange(option.value)
                          setOpen(false)
                        }}
                      >
                        {option.label}
                        <IconCheck
                          className={cn(
                            "ml-auto size-4",
                            field.value === option.value ? "opacity-100" : "opacity-0"
                          )}
                        />
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </CommandList>
              </Command>
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