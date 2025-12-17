import { useState, useCallback } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/permission-table-toolbar"
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"
import { tableColumns } from "./table/permission-table-columns"
import { type Permission, type PermissionCriteriaSchema } from "@/domains/permission"
import { PermissionService } from "@/services/permission-service"
import { RoleAdmin } from "@/layouts/permission/role-control"

export default function Permissions() {
  // State to hold the fetched data
  const entityName = "permission"
  const [tableData, setTableData] = useState([] as Array<Permission>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  const loadPage = useCallback((
    pageNo: number = 0,
    pageSize: number = 10,
    sorts: Array<string> = ["updatedAt,desc"],
    criteria: PermissionCriteriaSchema = {}
  ): void => {
    setCurrentPageNo(pageNo)
    PermissionService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      resourceType: criteria.resourceType || null,
      action: criteria.action || null
    }).then(r => {
      setTableData(r.data.content)
      setTotalCount(r.data.totalElements)
      setTotalPages(r.data.totalPages)
    })
  }, [])

  function save(formData: Permission): Promise<void> {
    return PermissionService.save(formData).then(() => {
      loadPage(currentPageNo)
    })
  }

  function deleteRow(row: Permission): Promise<void> {
    if (!row.id) {
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
    <RoleAdmin>
      <LayoutBody className="flex flex-col" fixedHeight>
        <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:gap-y-0">
          <PaginatedDataTable
            paginationMode="server"
            columns={tableColumns(entityName, save, deleteRow)}
            data={tableData}
            totalCount={totalCount}
            totalPages={totalPages}
            loadPage={loadPage}
            deleteRows={deleteRows}
          >
            <DataTableToolbar entityName={entityName} loadPage={loadPage} save={save} />
          </PaginatedDataTable>
        </div>
      </LayoutBody>
    </RoleAdmin>
  )
}
