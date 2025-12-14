import { useState, useEffect, useCallback } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"
import { tableColumns } from "./table/table-columns"
import { type Role, type RoleCriteriaSchema } from "@/domains/role"
import { RoleService } from "@/services/role-service"
import { filterTable } from "@/lib/table-utils"
import { RoleAdmin } from "@/permission/role-control"

export default function Role() {
  // State to hold the fetched data
  const entityName = "role"
  const [tableData, setTableData] = useState([] as Array<Role>)

  const loadPage = useCallback((criteria: RoleCriteriaSchema = {}) => {
    if (criteria.keyword) {
      setTableData(prev => filterTable(prev, criteria.keyword!))
      return
    }
    RoleService.find({ page: 0, size: 2000, sort: ["updatedAt,desc"] }).then(r => {
      setTableData(r.data)
    })
  }, [])

  useEffect(() => {
    loadPage()
  }, [loadPage])

  function save(formData: Role): Promise<void> {
    return RoleService.save(formData).then(() => {
      loadPage()
    })
  }

  function deleteRow(row: Role): Promise<void> {
    if (!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return RoleService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<Role>): Promise<Array<void>> {
    return Promise.all(rows.map(deleteRow))
  }

  return (
    <RoleAdmin>
      <LayoutBody className="flex flex-col" fixedHeight>
        <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-y-0">
          <PaginatedDataTable
            paginationMode="client"
            columns={tableColumns(entityName, save, deleteRow)}
            data={tableData}
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
