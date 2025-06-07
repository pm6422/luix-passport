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
import { Button } from "@/components/custom/button"
import { IconSelector, IconCheck, IconX } from "@tabler/icons-react"
import { cn } from "@/lib/utils"


type Props = {
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
}: Props) => {
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
          { showClearButton && (
            <IconX
              className="h-4 mr-1 cursor-pointer text-muted-foreground"
              onClick={event => {
                onValueChange && onValueChange("")
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
                if(query) {
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
                  onValueChange && onValueChange(option.value)
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
                        onValueChange && onValueChange("")
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
