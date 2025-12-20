import { ColumnDef } from "@tanstack/react-table"
import { PromoCode } from "@/domains/promo-code"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { Badge } from "@/components/ui/badge"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { Switch } from "@/components/ui/switch"
import { Button } from "@/components/ui/button"
import { Mail } from "lucide-react"

interface TableColumnsProps {
  toggleEnabled: (id: string, checked: boolean) => void
  onSendEmail: (promoCode: PromoCode) => void
}

export function getPromoCodeColumns({ toggleEnabled, onSendEmail }: TableColumnsProps): ColumnDef<PromoCode>[] {
  return [
    {
      accessorKey: "code",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Code" />
      ),
      cell: ({ row }) => <div className="font-mono">{row.getValue("code")}</div>,
      enableSorting: false,
      enableHiding: false,
    },
    {
      accessorKey: "type",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Type" />
      ),
      cell: ({ row }) => {
        return (
          <Badge variant="outline">{row.getValue("type")}</Badge>
        )
      },
      enableSorting: false,
    },
    {
      accessorKey: "durationDays",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Duration (Days)" />
      ),
      cell: ({ row }) => <div>{row.getValue("durationDays")}</div>,
      enableSorting: false,
    },
    {
      accessorKey: "batchId",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Batch ID" />
      ),
      cell: ({ row }) => <div>{row.getValue("batchId")}</div>,
      enableSorting: false,
    },
    {
      accessorKey: "enabled",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Enabled" />
      ),
      cell: ({ row }) => {
        const enabled = row.getValue("enabled") as boolean
        const id = row.original.id
        return (
          <div className="flex w-[50px] items-center justify-center" onClick={(e) => e.stopPropagation()}>
            <Switch
              checked={enabled}
              className="data-[state=checked]:bg-green-500"
              onCheckedChange={(checked: boolean) => {
                toggleEnabled(id, checked)
              }}
            />
          </div>
        )
      },
      enableSorting: true,
    },
    {
      accessorKey: "usedBy",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Used By" />
      ),
      cell: ({ row }) => <div>{row.getValue("usedBy") || "-"}</div>,
      enableSorting: false,
    },
    {
      accessorKey: "usedAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Used At" />
      ),
      cell: ({ row }) => {
        const date = row.getValue("usedAt") as string
        if (!date) return <div>-</div>
        return <div className="w-[100px]"><UserDateTime value={date} twoLines={true} /></div>
      },
      enableSorting: false,
    },
    {
      accessorKey: "expiresAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Expires At" />
      ),
      cell: ({ row }) => {
        const date = row.getValue("expiresAt") as string
        if (!date) return <div>-</div>
        return <div className="w-[100px]"><UserDateTime value={date} twoLines={true} /></div>
      },
      enableSorting: false,
    },
    {
      id: "actions",
      cell: ({ row }) => {
        const promoCode = row.original
        return (
          <div className="flex justify-end">
            <Button
              variant="secondary"
              className="flex size-8 p-0"
              onClick={(e) => {
                e.stopPropagation()
                onSendEmail(promoCode)
              }}
              title="Send Email"
              disabled={!!promoCode.usedBy || !!promoCode.usedAt}
            >
              <Mail className="size-4" />
              <span className="sr-only">Send Email</span>
            </Button>
          </div>
        )
      },
    }
  ]
}
