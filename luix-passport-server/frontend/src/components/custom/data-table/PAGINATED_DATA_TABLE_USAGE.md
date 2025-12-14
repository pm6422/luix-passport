# Paginated Data Table Component

A flexible data table component that supports both server-side and client-side pagination.

## Features

✅ Dual pagination modes  
✅ Type-safe discriminated union props  
✅ Row selection and batch delete  
✅ Column visibility toggles  
✅ Sorting and filtering support

## Usage

### Server Pagination

```tsx
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"

<PaginatedDataTable
  paginationMode="server"
  columns={columns}
  data={data}
  totalCount={totalCount}
  totalPages={totalPages}
  loadPage={(pageNo, pageSize, sorts, criteria) => {
    fetchData({ page: pageNo, size: pageSize, sort: sorts })
  }}
  deleteRows={async (rows) => {
    await Promise.all(rows.map(row => deleteRow(row.id)))
  }}
>
  <TableToolbar />
</PaginatedDataTable>
```

### Client Pagination

```tsx
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"

<PaginatedDataTable
  paginationMode="client"
  columns={columns}
  data={data}
  loadPage={(criteria) => {
    fetchAllData(criteria)
  }}
  deleteRows={async (rows) => {
    await Promise.all(rows.map(row => deleteRow(row.id)))
  }}
>
  <TableToolbar />
</PaginatedDataTable>
```

## Props

### Common

| Prop | Type | Required | Description |
| ---- | ---- | -------- | ----------- |
| `children` | `ReactNode` | Yes | Toolbar or content above the table |
| `columns` | `ColumnDef[]` | Yes | Column definitions |
| `data` | `TData[]` | Yes | Table data |
| `deleteRows` | `(rows: TData[]) => Promise<void[]>` | No | Batch delete function |

### Server Mode

| Prop | Type | Required | Description |
| ---- | ---- | -------- | ----------- |
| `paginationMode` | `"server"` | Yes | Enable server pagination |
| `totalCount` | `number` | Yes | Total number of records |
| `totalPages` | `number` | Yes | Total number of pages |
| `loadPage` | `(pageNo, pageSize, sorts?, criteria?) => void` | Yes | Load page callback |

### Client Mode

| Prop | Type | Required | Description |
| ---- | ---- | -------- | ----------- |
| `paginationMode` | `"client"` | Yes | Enable client pagination |
| `loadPage` | `(criteria?) => void` | Yes | Reload data callback |


## Files

- `paginated-data-table.tsx` — unified component
- `data-table-pagination.tsx` — pagination controls
- `data-table-view-options.tsx` — column visibility menu
