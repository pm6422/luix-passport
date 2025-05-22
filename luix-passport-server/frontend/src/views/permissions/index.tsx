import { useState } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type Permission, type PermissionCriteriaSchema } from "@/domains/permission"
import { PermissionService } from "@/services/permission-service"

export default function DataDict() {
  // State to hold the fetched data
  const entityName = "permission"
  const [tableData, setTableData] = useState([] as Array<Permission>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  function loadPage(pageNo: number = 0, pageSize: number = 10, sorts: Array<string> = ["modifiedAt,desc"], criteria: PermissionCriteriaSchema = {}): void {
    setCurrentPageNo(pageNo)
    PermissionService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      resourceType: criteria.resourceType || null,
      action: criteria.action || null
    }).then(r => {
      setTableData(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / pageSize))
    })
  }

  function save(formData: Permission): Promise<void> {
    return PermissionService.save(formData).then(() => {
      loadPage(currentPageNo)
    })
  }

  function deleteRow(row: Permission): Promise<void> {
    if(!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return PermissionService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<Permission>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-x-12 lg:space-y-0">
        <DataTable 
          columns={tableColumns(entityName, save, deleteRow)}
          data={tableData} 
          totalCount={totalCount} 
          totalPages={totalPages} 
          loadPage={loadPage} 
          deleteRows={deleteRows}
        >
          <DataTableToolbar entityName={entityName} loadPage={loadPage} save={save} />
        </DataTable>
      </div>
    </LayoutBody>
  )
}