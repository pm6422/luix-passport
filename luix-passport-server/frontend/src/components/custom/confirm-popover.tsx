import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import React from 'react'

type Props = {
  children: React.ReactNode
  message: string
  onClickYes: () => void
  onClickNo?: () => void
  showNoButton?: boolean
  open?: boolean
  onOpenChange?: (open: boolean) => void
}

export const ConfirmPopover = ({
                                 children,
                                 message,
                                 onClickYes,
                                 onClickNo,
                                 showNoButton = true,
                                 open,
                                 onOpenChange,
                               }: Props) => {
  return (
    <Popover open={open} onOpenChange={onOpenChange}>
      <PopoverTrigger asChild>{children}</PopoverTrigger>
      <PopoverContent className="max-w-[300px] p-4">
        <p className="text-sm">{message}</p>
        <div className="mt-4 flex flex-wrap gap-2">
          {showNoButton && onClickNo && (
            <Button
              className="flex-1 min-w-[100px]"
              variant="secondary"
              size="sm"
              onClick={onClickNo}
            >
              No
            </Button>
          )}
          <Button
            className="flex-1 min-w-[100px]"
            variant="destructive"
            size="sm"
            onClick={onClickYes}
          >
            Yes
          </Button>
        </div>
      </PopoverContent>
    </Popover>
  )
}