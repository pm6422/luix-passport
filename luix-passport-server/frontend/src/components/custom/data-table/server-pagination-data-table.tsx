/**
 * Server Pagination DataTable
 * 
 * This is a compatibility wrapper for the unified DataTable component.
 * Use this for server-side pagination scenarios.
 * 
 * @deprecated Consider using the unified DataTable component directly with paginationMode="server"
 */

import { DataTable as UnifiedDataTable } from "./data-table"
import { ColumnDef } from "@tanstack/react-table"
import { BaseCriteria } from "@/domains/base/base-criteria"
import React from "react"

interface DataTableProps<TData, TValue> {
  children: React.ReactNode
  columns: ColumnDef<TData, TValue>[]
  data: TData[]
  totalCount: number
  totalPages: number
  loadPage: (
    pageNo: number,
    pageSize: number,
    sorts?: string[],
    criteria?: BaseCriteria
  ) => void
  deleteRows?: (rows: TData[]) => Promise<void[]>
}

export function DataTable<TData, TValue>(props: DataTableProps<TData, TValue>) {
  return (
    <UnifiedDataTable
      paginationMode="server"
      children={props.children}
      columns={props.columns}
      data={props.data}
      totalCount={props.totalCount}
      totalPages={props.totalPages}
      loadPage={props.loadPage}
      deleteRows={props.deleteRows}
    />
  )
}
