import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/ui/button"
import { StatusBadge } from "@/components/custom/status-badge"
import { Auth2Client } from "@/domains/auth2-client"
import { EditDialog } from "../dialog/edit-dialog"

export function tableColumns(
  entityName: string,
  save: (formData: Auth2Client) => Promise<void>,
  deleteRow: (row: Auth2Client) => Promise<void>,
): ColumnDef<Auth2Client>[] {
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
      accessorKey: "clientId",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Client ID" />
      ),
      cell: ({ row }) => <div className="w-[170px]">{row.original.clientId}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "clientName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Client Name" />
      ),
      cell: ({ row }) => <div className="w-[180px]">{row.original.clientName}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "clientAuthenticationMethods",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Authentication Methods" />
      ),
      cell: ({ row }) => (
        <div className="w-[100px] text-xs">
          {(row.original.clientAuthenticationMethods as string[]).map((item, index) => (
            <div key={index}>{item}</div>
          ))}
        </div>
      ),
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "authorizationGrantTypes",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Authentication Grant Types" />
      ),
      cell: ({ row }) => (
        <div className="w-[130px] text-xs">
          {(row.original.authorizationGrantTypes as string[]).map((item, index) => (
            <div key={index}>{item}</div>
          ))}
        </div>
      ),
      enableSorting: false,
      enableHiding: true,
    },
    {
      accessorKey: "scopes",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Scopes" />
      ),
      cell: ({ row }) => (
        <div className="w-[75px] text-xs">
          {(row.original.scopes as string[]).map((item, index) => (
            <div key={index}>{item}</div>
          ))}
        </div>
      ),
      enableSorting: false,
      enableHiding: true,
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
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions<Auth2Client> entityName={entityName} row={row} deleteRow={deleteRow}>
          <EditDialog entityName={entityName} id={row.original.id} save={save}>
            <Button variant="secondary" className="flex size-8 p-0">
              <IconEdit className="size-4" />
              <span className="sr-only">Update</span>
            </Button>
          </EditDialog>
        </DataTableRowActions>
      )
    }
  ]
}
