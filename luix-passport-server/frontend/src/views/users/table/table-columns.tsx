import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit, IconDots } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/custom/button"
import { yesNo } from "@/data/yes-no"
import { User } from "@/domains/user"
import { EditDialog } from "../dialog/edit-dialog"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger
} from "@/components/ui/dropdown-menu"
import { ConfirmPopover } from "@/components/custom/confirm-popover"
import { toast } from "sonner"
import { getErrorMessage } from "@/libs/handle-error"
import { DateTime } from "@/components/custom/date-time"

export function tableColumns(
  entityName: string,
  save: (formData: User) => Promise<void>,
  deleteRow: (row: User) => Promise<void>,
  resetPassword: (row: User) => Promise<void>
): ColumnDef<User>[] {

  function clickResetYes(row: User): void {
    toast.promise(resetPassword(row), {
      loading: "Resetting password ...",
      success: () => {
        return "Reset password"
      },
      error: (error) => {
        return getErrorMessage(error)
      }
    })
  }

  return [
    {
      id: "select",
      header: ({ table }) => (
        <Checkbox
          checked={
            table.getIsAllPageRowsSelected() ||
            (table.getIsSomePageRowsSelected() && "indeterminate")
          }
          onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
          aria-label="Select all"
          className="translate-y-[2px]"
        />
      ),
      cell: ({ row }) => (
        <Checkbox
          checked={row.getIsSelected()}
          onCheckedChange={(value) => row.toggleSelected(!!value)}
          aria-label="Select row"
          className="translate-y-[2px]"
        />
      ),
      enableSorting: false,
      enableHiding: false,
    },
    {
      accessorKey: "username",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Username" />
      ),
      cell: ({ row }) => <div className="w-[30px]">{row.getValue("username")}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "email",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Email" />
      ),
      cell: ({ row }) => <div className="w-[125px]">{row.getValue("email")}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "mobileNo",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Mobile No" />
      ),
      cell: ({ row }) => <div className="w-[125px]">{row.getValue("mobileNo")}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "firstName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="First Name" />
      ),
      cell: ({ row }) => <div className="w-[50px]">{row.getValue("firstName")}</div>,
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "lastName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Last Name" />
      ),
      cell: ({ row }) => <div className="w-[50px]">{row.getValue("lastName")}</div>,
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "roles",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Roles" />
      ),
      cell: ({ row }) => (
        <div className="w-[100px] text-xs">
          {(row.getValue("roles") as string[]).map((item, index) => (
            <div key={index}>{item}</div>
          ))}
        </div>
      ),
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "activated",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Activated" />
      ),
      cell: ({ row }) => {
        const element = yesNo.find(e => e.value === row.getValue("activated"))

        return (
          <div className="flex w-[50px] items-center justify-center">
            {element && element.icon && (
              <element.icon className="mr-2 size-5 text-muted-foreground" />
            )}
          </div>
        )
      },
      filterFn: (row, id, value) => {
        return value.includes(row.getValue(id))
      },
    },
    {
      accessorKey: "enabled",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Enabled" />
      ),
      cell: ({ row }) => {
        const element = yesNo.find(e => e.value === row.getValue("enabled"))

        return (
          <div className="flex w-[50px] items-center justify-center">
            {element && element.icon && (
              <element.icon className="mr-2 size-5 text-muted-foreground" />
            )}
          </div>
        )
      },
      filterFn: (row, id, value) => {
        return value.includes(row.getValue(id))
      },
    },
    {
      accessorKey: "lastSignInAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Last Sign In" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.getValue("lastSignInAt")}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "modifiedAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Modified At" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.getValue("modifiedAt")}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions entityName={entityName} row={row} deleteRow={deleteRow} 
          children={
            <EditDialog entityName={entityName} id={row.original.id} save={save}>
              <Button variant="secondary" className="flex size-8 p-0">
                <IconEdit className="size-4" />
                <span className="sr-only">Update</span>
              </Button>
            </EditDialog>
          } 
          moreActions={
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="secondary"
                  className="flex size-8 p-0 data-[state=open]:bg-muted"
                >
                  <IconDots className="size-4" />
                  <span className="sr-only">Open menu</span>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-fit space-y-1">
                <ConfirmPopover 
                  message={"Reset password?"}
                  onClickYes={() => clickResetYes(row.original)}
                >
                  <Button variant="secondary">Reset password</Button>
                </ConfirmPopover>
              </DropdownMenuContent>
            </DropdownMenu>
          }
        >
        </DataTableRowActions>
      )
    }
  ]
}