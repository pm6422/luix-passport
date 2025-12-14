import { useState, useEffect } from "react"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandSeparator
} from "@/components/ui/command"
import { Separator } from "@/components/ui/separator"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Button } from "@/components/ui/button"
import { IconSelector, IconCheck, IconX } from "@tabler/icons-react"
import { cn } from "@/lib/utils"
import type { Control, FieldValues, Path } from "react-hook-form"
import { FormControl, FormDescription, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { RequiredFormLabel } from "../required-form-label"
import { Key } from "react"

// ----- ClearableSelect Component -----

type ClearableSelectProps = {
  options: { label: string; value: string; }[]
  defaultValue?: string
  onValueChange?: (value: string) => void
  placeholder?: string
};

export const ClearableSelect = ({
  options,
  defaultValue,
  onValueChange,
  placeholder = "Select..."
}: ClearableSelectProps) => {
  const [isPopoverOpen, setIsPopoverOpen] = useState(false)
  const [query, setQuery] = useState<string>(defaultValue || "")
  const [showSelectButton, setShowSelectButton] = useState(true)
  const [showClearButton, setShowClearButton] = useState(false)

  useEffect(() => {
    setQuery(defaultValue || "")
  }, [defaultValue])

  return (
    <Popover open={isPopoverOpen} onOpenChange={setIsPopoverOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          className="flex w-full rounded-md border min-h-9 h-auto py-0 px-1 items-center justify-between bg-inherit hover:bg-card shadow-xs"
          onClick={() => setIsPopoverOpen(!isPopoverOpen)}
        >
          <span className="text-sm font-normal px-2">
            {query
              ? options.find(
                (option) => option.value === query
              )?.label
              : placeholder}
          </span>
          {showClearButton && (
            <IconX
              className="h-4 mr-1 cursor-pointer text-muted-foreground"
              onClick={event => {
                if (onValueChange) {
                  onValueChange("")
                }
                event.stopPropagation()
              }}
              onMouseLeave={() => {
                setShowClearButton(false)
                setShowSelectButton(true)
              }}
            />
          )}
          {showSelectButton && (
            <IconSelector
              className="h-4 mr-1 cursor-pointer text-muted-foreground"
              onMouseEnter={() => {
                if (query) {
                  setShowSelectButton(false)
                  setShowClearButton(true)
                }
              }}
            />
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="min-w-fit p-0 drop-shadow-xs" align="start">
        <Command>
          <CommandInput placeholder="Search..." />
          <CommandEmpty>No results found.</CommandEmpty>
          <ScrollArea className="max-h-72 p-1">
            {options.map((option) => (
              <CommandItem
                value={option.label}
                key={option.value}
                onSelect={() => {
                  setQuery(option.value)
                  if (onValueChange) {
                    onValueChange(option.value)
                  }
                }}
              >
                <IconCheck
                  className={cn(
                    "mr-2 size-4",
                    option.value === query
                      ? "opacity-100"
                      : "opacity-0"
                  )}
                />
                {option.label}
              </CommandItem>
            ))}
          </ScrollArea>
          <CommandSeparator />
          <CommandGroup>
            <div className="flex items-center justify-between">
              {query && (
                <>
                  <CommandItem
                    onSelect={() => {
                      if (onValueChange) {
                        onValueChange("")
                      }
                    }}
                    style={{
                      pointerEvents: "auto",
                      opacity: 1,
                    }}
                    className="flex-1 justify-center cursor-pointer"
                  >
                    Clear
                  </CommandItem>
                  <Separator orientation="vertical" className="flex min-h-6 h-full" />
                </>
              )}
              <CommandSeparator />
              <CommandItem
                onSelect={() => setIsPopoverOpen(false)}
                style={{
                  pointerEvents: "auto",
                  opacity: 1,
                }}
                className="flex-1 justify-center cursor-pointer"
                style={{
                  pointerEvents: "auto",
                  opacity: 1,
                }}
              >
                Close
              </CommandItem>
            </div>
          </CommandGroup>
        </Command>
      </PopoverContent>
    </Popover>
  )
}

// ----- FormField Wrapper -----

interface Props<TFieldValues extends FieldValues> {
  control: Control<TFieldValues>
  name: keyof TFieldValues
  key?: Key
  label?: string
  options: {
    label: string
    value: string
  }[]
  description?: string
  placeholder?: string
  required?: boolean
  disabled?: boolean
  selectContentClassName?: string
  formItemClassName?: string
  hide?: boolean
}

const ClearableSelectFormField = <TFieldValues extends FieldValues>({
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
}: Props<TFieldValues>) => (!hide &&
  <FormField
    control={disabled ? undefined : control}
    name={name as Path<TFieldValues>}
    key={key}
    render={({ field }) => (
      <FormItem className={formItemClassName}>
        {label && <RequiredFormLabel required={required}>{label}</RequiredFormLabel>}
        <FormControl>
          <ClearableSelect options={options} defaultValue={field.value} onValueChange={field.onChange} placeholder={placeholder} />
        </FormControl>
        {description && <FormDescription>{description}</FormDescription>}
        <FormMessage />
      </FormItem>
    )}
  />
)

export default ClearableSelectFormField
