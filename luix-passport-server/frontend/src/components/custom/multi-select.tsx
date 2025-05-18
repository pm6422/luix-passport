import { useState, useEffect, useRef, forwardRef } from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { IconCheck, IconX, IconCircleX, IconSelector, IconCirclePlus } from "@tabler/icons-react"
import { cn } from "@/lib/utils"
import { Separator } from "@/components/ui/separator"
import { Button } from "@/components/custom/button"
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
  "m-1 transition ease-in-out delay-150 hover:-translate-y-1 hover:scale-110 duration-300 shadow-sm",
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

interface MultiSelectProps extends React.ButtonHTMLAttributes<HTMLButtonElement>, VariantProps<typeof multiSelectVariants> {
  asChild?: boolean
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
    asChild = false,
    options,
    defaultValue,
    onValueChange,
    disabled,
    placeholder,
    creatable,
    multiple,
    ...props
  },
  ref
) => {
  const [selectedValues, setSelectedValues] = useState<string[]>(Array.isArray(defaultValue) ? defaultValue : defaultValue ? [defaultValue] : [])
  const selectedValuesSet = useRef(new Set(selectedValues))
  const [isPopoverOpen, setIsPopoverOpen] = useState(false)
  const [query, setQuery] = useState<string>("")
  const [showSelectButton, setShowSelectButton] = useState(true)
  const [showClearButton, setShowClearButton] = useState(false)

  useEffect(() => {
    setSelectedValues(Array.isArray(defaultValue) ? defaultValue : defaultValue ? [defaultValue] : [])
    selectedValuesSet.current = new Set(Array.isArray(defaultValue) ? defaultValue : defaultValue ? [defaultValue] : [])
  }, [defaultValue])

  const handleInputKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      setIsPopoverOpen(true)
    } else if (event.key === "Backspace" && !event.currentTarget.value) {
      const lastValue = selectedValues[selectedValues.length - 1]
      setSelectedValues(prev => prev.slice(0, -1))
      selectedValuesSet.current.delete(lastValue)
      if(multiple) {
        onValueChange(selectedValues.filter(v => v !== lastValue))
      } else {
        onValueChange("")
      }
    }
  }

  const toggleOption = (value: string) => {
    if (selectedValuesSet.current.has(value)) {
      selectedValuesSet.current.delete(value)
      setSelectedValues(selectedValues.filter(v => v !== value))
    } else {
      if (multiple || selectedValues.length === 0) {
        selectedValuesSet.current.add(value)
        setSelectedValues([...selectedValues, value])
      }
    }
    if(multiple) {
      onValueChange([...selectedValuesSet.current])
    } else {
      onValueChange(value)
      setIsPopoverOpen(false)
    }
  }

  const handleClearAll = () => {
    setSelectedValues([])
    selectedValuesSet.current.clear()
    if(multiple) {
      onValueChange([])
    } else {
      onValueChange("")
    }
  }

  return (
    <Popover open={isPopoverOpen} onOpenChange={setIsPopoverOpen}>
      <PopoverTrigger asChild>
        <Button
          ref={ref}
          {...props}
          onClick={() => setIsPopoverOpen(!isPopoverOpen)}
          disabled={disabled}
          className="flex w-full rounded-md border min-h-9 h-auto py-0 px-1 items-center justify-between bg-inherit hover:bg-card shadow-sm"
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
                      className={cn("", multiSelectVariants({ variant, className }))}
                    >
                      {IconComponent && <IconComponent className="size-4 mr-1" />}
                      {creatable ? (option ? option.label : value) : option?.label}
                      <IconCircleX
                        className="ml-1 size-4 cursor-pointer"
                        onClick={event => {
                          event.stopPropagation()
                          const newValues = selectedValues.filter(v => v !== value)
                          setSelectedValues(newValues)
                          selectedValuesSet.current = new Set(newValues)
                          if(multiple) {
                            onValueChange(newValues)
                          } else {
                            onValueChange("")
                          }
                        }}
                      />
                    </Badge>
                  )
                })}
              </div>
              <div className="flex items-center justify-between">
                { showClearButton && (
                  <IconX
                    className="h-4 mr-1 cursor-pointer text-muted-foreground"
                    onClick={event => {
                      handleClearAll()
                      event.stopPropagation()
                    }}
                    onMouseLeave={() => {
                      setShowClearButton(false)
                      setShowSelectButton(true)
                    }}
                  />
                )}
                { showSelectButton && (
                  <IconSelector 
                    className="h-4 mr-1 cursor-pointer text-muted-foreground"
                    onMouseEnter={() => {
                      setShowSelectButton(false)
                      setShowClearButton(true)
                    }}
                  />
                )}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-between w-full mx-auto">
              <span className="text-sm text-muted-foreground px-2">{placeholder}</span>
              <IconSelector 
                className="h-4 mr-1 cursor-pointer text-muted-foreground"
              />
            </div>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent
        className="min-w-fit p-0 drop-shadow-sm"
        align="start"
        onEscapeKeyDown={() => setIsPopoverOpen(false)}
        onInteractOutside={event => {
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
            onValueChange={(value: string) => setQuery(value)}
          />
          <CommandList>
            {creatable ? (
              <CommandEmpty
                onClick={() => {
                  setSelectedValues([...selectedValues, query])
                  if(multiple) {
                    onValueChange([...selectedValues, query])
                  } else {
                    onValueChange(query)
                  }
                  setQuery("")
                }}
                className="flex cursor-pointer items-center justify-center gap-1 my-1"
              >
                <Button variant="ghost" className="flex-1 justify-center cursor-pointer gap-2">
                  <IconCirclePlus className="size-5" />
                  {query}
                </Button>
              </CommandEmpty>
            ) : (
              <CommandEmpty>No results found.</CommandEmpty>
            )}

            <CommandGroup>
              {options.map(option => {
                const isSelected = selectedValuesSet.current.has(option.value)
                return (
                  <CommandItem
                    key={option.value}
                    onSelect={() => toggleOption(option.value)}
                    style={{
                      pointerEvents: "auto",
                      opacity: 1,
                    }}
                    className="cursor-pointer"
                  >
                    <div
                      className={cn(
                        "mr-2 flex size-4 items-center justify-center rounded-sm border border-primary",
                        isSelected ? "bg-primary text-primary-foreground" : "opacity-50 [&_svg]:invisible"
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
