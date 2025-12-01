import { useState } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type User, type UserCriteriaSchema } from "@/domains/user"
import { UserService } from "@/services/user-service"

export default function DataDict() {
  // State to hold the fetched data
  const entityName = "user"
  const [tableData, setTableData] = useState([] as Array<User>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  function loadPage(pageNo: number = 0, pageSize: number = 10, sorts: Array<string> = ["updatedAt,desc"], criteria: UserCriteriaSchema = {}): void {
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
      setTableData(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / pageSize))
    })
  }

  function save(formData: User): Promise<void> {
    return UserService.save(formData).then(() => {
      loadPage(currentPageNo)
    })
  }

  function deleteRow(row: User): Promise<void> {
    if(!row.username) {
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
    if(!row.username) {
      return Promise.reject("Invalid empty id")
    }
    return UserService.resetPassword(row.username)
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-y-0">
        <DataTable 
          columns={tableColumns(entityName, save, deleteRow, resetPassword)} 
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