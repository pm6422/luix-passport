import { useState, useCallback } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/user-table-toolbar"
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"
import { tableColumns } from "./table/user-table-columns"
import { type User, type UserCriteriaSchema } from "@/domains/user"
import { UserService } from "@/services/user-service"
import { RoleAdmin } from "@/layouts/permission/role-control"

export default function DataDict() {
  // State to hold the fetched data
  const entityName = "user"
  const [tableData, setTableData] = useState([] as Array<User>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  const loadPage = useCallback((
    pageNo: number = 0,
    pageSize: number = 10,
    sorts: Array<string> = ["updatedAt,desc"],
    criteria: UserCriteriaSchema = {}
  ): void => {
    setCurrentPageNo(pageNo)
    UserService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      username: criteria.username || null,
      email: criteria.email || null,
      mobileNo: criteria.mobileNo || null,
      enabled: criteria.enabled || null
    }).then(r => {
      setTableData(r.data.content)
      setTotalCount(r.data.totalElements)
      setTotalPages(r.data.totalPages)
    })
  }, [])

  function save(formData: User): Promise<void> {
    return UserService.save(formData).then(() => {
      loadPage(currentPageNo)
    })
  }

  function deleteRow(row: User): Promise<void> {
    if (!row.username) {
      return Promise.reject("Invalid empty id")
    }
    return UserService.deleteById(row.username).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<User>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  function resetPassword(row: User): Promise<void> {
    if (!row.username) {
      return Promise.reject("Invalid empty id")
    }
    return UserService.resetPassword(row.username)
  }

  return (
    <RoleAdmin>
      <LayoutBody className="flex flex-col" fixedHeight>
        <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:gap-y-0">
          <PaginatedDataTable
            paginationMode="server"
            columns={tableColumns(entityName, save, deleteRow, resetPassword)}
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
