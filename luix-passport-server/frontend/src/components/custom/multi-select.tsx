import { useState, useEffect, useRef, forwardRef, useCallback } from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { IconCheck, IconX, IconCircleX, IconSelector, IconCirclePlus } from "@tabler/icons-react"
import { cn } from "@/lib/utils"
import { Separator } from "@/components/ui/separator"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from "@/components/ui/command"

const multiSelectVariants = cva(
  "m-1 transition ease-in-out delay-150 hover:-translate-y-1 hover:scale-110 duration-300 shadow-xs font-normal",
  {
    variants: {
      variant: {
        default: "border-foreground/10 text-foreground bg-card hover:bg-card/80",
        secondary: "border-foreground/10 bg-secondary text-secondary-foreground hover:bg-secondary/80",
        destructive: "border-transparent bg-destructive text-destructive-foreground hover:bg-destructive/80",
        inverted: "inverted",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

export type Option = {
  label: string
  value: string
  icon?: React.ComponentType<{ className?: string }>
}

interface MultiSelectProps extends React.ButtonHTMLAttributes<HTMLButtonElement>, VariantProps<typeof multiSelectVariants> {
  options: { label: string; value: string; icon?: React.ComponentType<{ className?: string }> }[];
  defaultValue?: string | string[]
  disabled?: boolean
  placeholder?: string
  onValueChange: (value: string | string[]) => void
  creatable?: boolean
  multiple?: boolean
}

const MultiSelect: React.ForwardRefRenderFunction<HTMLButtonElement, MultiSelectProps> = (
  {
    className,
    variant,
    options,
    defaultValue,
    onValueChange,
    disabled,
    placeholder = "Select...",
    creatable,
    multiple,
    ...props
  },
  ref
) => {
  const initialValues = Array.isArray(defaultValue) ? defaultValue : defaultValue ? [defaultValue] : []
  const [selectedValues, setSelectedValues] = useState<string[]>(initialValues)
  const selectedValuesSet = useRef(new Set(initialValues))
  const [isPopoverOpen, setIsPopoverOpen] = useState(false)
  const [query, setQuery] = useState("")
  const [showClearButton, setShowClearButton] = useState(false)

  useEffect(() => {
    const newValues = Array.isArray(defaultValue) ? defaultValue : defaultValue ? [defaultValue] : []
    setSelectedValues(newValues)
    selectedValuesSet.current = new Set(newValues)
  }, [defaultValue])

  const handleInputKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      setIsPopoverOpen(true)
    } else if (event.key === "Backspace" && !query) {
      const lastValue = selectedValues[selectedValues.length - 1]
      if (lastValue) {
        const newValues = selectedValues.slice(0, -1)
        setSelectedValues(newValues)
        selectedValuesSet.current = new Set(newValues)
        onValueChange(multiple ? newValues : "")
      }
    }
  }

  const toggleOption = useCallback((value: string) => {
    const newSelectedValues = selectedValuesSet.current.has(value)
      ? selectedValues.filter(v => v !== value)
      : multiple
        ? [...selectedValues, value]
        : [value]

    selectedValuesSet.current = new Set(newSelectedValues)
    setSelectedValues(newSelectedValues)

    if (multiple) {
      onValueChange(newSelectedValues)
    } else {
      onValueChange(newSelectedValues[0] || "")
      setIsPopoverOpen(false)
    }
  }, [multiple, onValueChange, selectedValues])

  const handleClearAll = useCallback(() => {
    setSelectedValues([])
    selectedValuesSet.current = new Set()
    onValueChange(multiple ? [] : "")
  }, [multiple, onValueChange])

  const handleRemoveValue = useCallback((value: string) => {
    const newValues = selectedValues.filter(v => v !== value)
    setSelectedValues(newValues)
    selectedValuesSet.current = new Set(newValues)
    onValueChange(multiple ? newValues : "")
  }, [multiple, onValueChange, selectedValues])

  const handleCreate = useCallback(() => {
    if (!query) return

    const newValues = multiple ? [...selectedValues, query] : [query]
    setSelectedValues(newValues)
    selectedValuesSet.current = new Set(newValues)
    onValueChange(multiple ? newValues : query)
    setQuery("")
    if (!multiple) setIsPopoverOpen(false)
  }, [query, multiple, onValueChange, selectedValues])

  return (
    <Popover open={isPopoverOpen} onOpenChange={setIsPopoverOpen}>
      <PopoverTrigger asChild>
        <Button
          ref={ref}
          {...props}
          onClick={() => setIsPopoverOpen(!isPopoverOpen)}
          disabled={disabled}
          className="flex w-full rounded-md border min-h-9 h-auto py-0 px-1 items-center justify-between bg-inherit hover:bg-card shadow-xs"
        >
          {selectedValues.length > 0 ? (
            <div className="flex justify-between items-center w-full">
              <div className="flex flex-wrap items-center">
                {selectedValues.map(value => {
                  const option = options.find(o => o.value === value)
                  const IconComponent = option?.icon
                  return (
                    <Badge
                      key={value}
                      className={cn(multiSelectVariants({ variant, className }))}
                    >
                      {IconComponent && <IconComponent className="size-4 mr-1" />}
                      {creatable ? (option ? option.label : value) : option?.label}
                      <IconCircleX
                        className="ml-1 size-4"
                        onClick={(event) => {
                          event.stopPropagation()
                          handleRemoveValue(value)
                        }}
                      />
                    </Badge>
                  )
                })}
              </div>
              <div className="flex items-center justify-between">
                {showClearButton ? (
                  <IconX
                    className="size-4 mr-3 text-muted-foreground"
                    onClick={(event) => {
                      event.stopPropagation()
                      handleClearAll()
                    }}
                    onMouseLeave={() => setShowClearButton(false)}
                  />
                ) : (
                  <IconSelector
                    className="size-4 mr-3 text-muted-foreground"
                    onMouseEnter={() => setShowClearButton(true)}
                  />
                )}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-between w-full mx-auto">
              <span className="text-sm text-muted-foreground px-2">{placeholder}</span>
              <IconSelector className="size-4 mr-3 text-muted-foreground" />
            </div>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent
        className="min-w-fit p-0 drop-shadow-xs"
        align="start"
        onEscapeKeyDown={() => setIsPopoverOpen(false)}
        onInteractOutside={(event) => {
          if (!event.defaultPrevented) {
            setIsPopoverOpen(false)
          }
        }}
      >
        <Command>
          <CommandInput
            placeholder="Search..."
            onKeyDown={handleInputKeyDown}
            value={query}
            onValueChange={setQuery}
          />
          <CommandList>
            {creatable && query && (
              <CommandEmpty
                onClick={handleCreate}
                className="flex items-center justify-center gap-1 my-1"
              >
                <Button variant="ghost" className="flex-1 justify-center gap-2">
                  <IconCirclePlus className="size-5" />
                  {query}
                </Button>
              </CommandEmpty>
            )}

            {!creatable && <CommandEmpty>No results found.</CommandEmpty>}

            <CommandGroup>
              {options.map((option) => {
                const isSelected = selectedValuesSet.current.has(option.value)
                return (
                  <CommandItem
                    key={option.value}
                    onSelect={() => toggleOption(option.value)}
                    className="cursor-pointer"
                  >
                    <div
                      className={cn(
                        "mr-2 flex size-4 items-center justify-center rounded-sm border border-primary",
                        isSelected
                          ? "text-primary-foreground dark:bg-primary"
                          : "opacity-50 [&_svg]:invisible dark:opacity-70"
                      )}
                    >
                      <IconCheck className="size-4" />
                    </div>
                    {option.icon && <option.icon className="mr-2 size-4 text-muted-foreground" />}
                    <span>{option.label}</span>
                  </CommandItem>
                )
              })}
            </CommandGroup>

            <CommandSeparator />

            <CommandGroup>
              <div className="flex items-center justify-between gap-2">
                {selectedValues.length > 0 && (
                  <>
                    <CommandItem
                      onSelect={handleClearAll}
                      className="flex-1 justify-center"
                    >
                      Clear
                    </CommandItem>
                    <Separator orientation="vertical" className="flex min-h-6 h-full" />
                  </>
                )}
                <CommandItem
                  onSelect={() => setIsPopoverOpen(false)}
                  className="flex-1 justify-center"
                >
                  Close
                </CommandItem>
              </div>
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  )
}

MultiSelect.displayName = "MultiSelect"

export default forwardRef<HTMLButtonElement, MultiSelectProps>(MultiSelect)