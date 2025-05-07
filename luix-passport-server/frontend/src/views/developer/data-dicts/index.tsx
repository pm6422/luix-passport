import { useState } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { DataTableToolbar } from "./table/table-toolbar"
import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"
import { tableColumns } from "./table/table-columns"
import { type DataDict, type DataDictCriteria } from "@/domains/data-dict"
import { DataDictService } from "@/services/data-dict-service"
import { type UploadFormSchema } from "@/components/custom/uploader/upload-dialog"

export default function DataDict() {
  // State to hold the fetched data
  const entityName = "data dictionary"
  const [tableData, setTableData] = useState([] as Array<DataDict>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  function loadPage(pageNo: number = 0, pageSize: number = 10, sorts: Array<string> = ["modifiedAt,desc"], criteria: DataDictCriteria = {}): void {
    setCurrentPageNo(pageNo)
    DataDictService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      num: criteria.num || null,
      categoryCode: criteria.categoryCode || null,
      enabled: criteria.enabled || null,
    }).then(r => {
      setTableData(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / pageSize))
    })
  }

  function save(formData: DataDict): Promise<void> {
    return DataDictService.save(formData).then(() => {
      loadPage(currentPageNo)
    })
  }

  function upload(formData: UploadFormSchema): Promise<Array<void>> {
    const promises: Array<Promise<void>> = formData.files.map(file => {
      const formData = new FormData()
      formData.append("file", file)
      return DataDictService.upload(formData)
    })
    return Promise.all(promises)
  }

  function deleteRow(row: DataDict): Promise<void> {
    if(!row.id) {
      return Promise.reject("Invalid empty id")
    }
    return DataDictService.deleteById(row.id).then(() => {
      loadPage()
    })
  }

  function deleteRows(rows: Array<DataDict>): Promise<Array<void>> {
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
          <DataTableToolbar entityName={entityName} loadPage={loadPage} save={save} upload={upload}/>
        </DataTable>
      </div>
    </LayoutBody>
  )
}