import { ColumnDef } from "@tanstack/react-table";
import { IconEdit, IconDots } from "@tabler/icons-react";
import { User } from "@/domains/user";
import { toast } from "sonner";
import { getErrorMessage } from "@/lib/handle-error";
import { StatusBadge } from "@/components/custom/status-badge";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { ConfirmPopover } from "@/components/custom/confirm-popover";
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header";
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions";
import { DateTime } from "@/components/custom/date-time";
import { EditDialog } from "../dialog/edit-dialog";

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
      cell: ({ row }) => <div className="w-[30px]">{row.original.username}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "email",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Email" />
      ),
      cell: ({ row }) => <div className="w-[130px]">{row.original.email}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "mobileNo",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Mobile No" />
      ),
      cell: ({ row }) => <div className="w-[125px]">{row.original.mobileNo}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "firstName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="First Name" />
      ),
      cell: ({ row }) => <div className="w-[50px]">{row.original.firstName}</div>,
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "lastName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Last Name" />
      ),
      cell: ({ row }) => <div className="w-[50px]">{row.original.lastName}</div>,
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "roleIds",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Roles" />
      ),
      cell: ({ row }) => (
        <div className="w-[100px] text-xs">
          {(row.original.roleIds as string[]).map((item, index) => (
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
        return (
          <div className="flex w-[75px] items-center justify-center">
            <StatusBadge value={row.original.activated} />
          </div>
        )
      },
    },
    {
      accessorKey: "enabled",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Enabled" />
      ),
      cell: ({ row }) => {
        return (
          <div className="flex w-[50px] items-center justify-center">
            <StatusBadge value={row.original.enabled} />
          </div>
        )
      },
    },
    {
      accessorKey: "lastSignInAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Last Sign In" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.original.lastSignInAt}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "modifiedAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Modified At" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.original.modifiedAt}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions<User> entityName={entityName} row={row} deleteRow={deleteRow}
          children={
            <EditDialog entityName={entityName} id={row.original.username} save={save}>
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