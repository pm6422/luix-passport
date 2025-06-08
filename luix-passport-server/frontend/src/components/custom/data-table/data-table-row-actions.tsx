import React, { useState } from "react"
import { IconTrash } from "@tabler/icons-react"
import { Row } from "@tanstack/react-table"
import { Button } from "@/components/ui/button"
import { ConfirmPopover } from "@/components/custom/confirm-popover"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"

interface DataTableRowActionsProps<TData> {
  children: React.ReactNode,
  moreActions?: React.ReactNode,
  entityName: string,
  row: Row<TData>,
  deleteRow: (row: TData) => Promise<void>
}

export function DataTableRowActions<TData>({
  children,
  moreActions,
  entityName,
  row,
  deleteRow
}: DataTableRowActionsProps<TData>) {
  const [delConfirmPopoverOpen, setDelConfirmPopoverOpen] = useState(false)

  function clickDeleteYes(): void {
    toast.promise(deleteRow(row.original), {
      loading: "Deleting " + entityName + "...",
      success: () => {
        setDelConfirmPopoverOpen(false)
        return "Deleted " + entityName
      },
      error: (error) => {
        setDelConfirmPopoverOpen(false)
        return getErrorMessage(error)
      }
    })
  }

  function clickDeleteNo(): void {
    setDelConfirmPopoverOpen(false)
  }

  return (
    <div className="flex items-center space-x-2">
      {children}
      <ConfirmPopover 
        message={"Are your sure to delete it?"}
        onClickYes={clickDeleteYes} 
        onClickNo={clickDeleteNo} 
        open={delConfirmPopoverOpen} 
        onOpenChange={setDelConfirmPopoverOpen}
      >
        <Button variant="secondary" className="flex size-8 p-0">
          <IconTrash className="size-4" />
          <span className="sr-only">Delete</span>
        </Button>
      </ConfirmPopover>
      {moreActions}
    </div>
  )
}
