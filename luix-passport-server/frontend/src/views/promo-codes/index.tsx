import { useState, useCallback } from "react"
import dayjs from "dayjs"
import { LayoutBody } from "@/layouts/layout-definitions"
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"
import { tableColumns } from "./table/promo-code-table-columns"
import { PromoCode, GeneratePromoCode } from "@/domains/promo-code"
import { PromoCodeService } from "@/services/promo-code-service"
import { RoleAdmin } from "@/layouts/permission/role-control"
import { DataTableToolbar } from "./table/promo-code-table-toolbar"

export default function PromoCodes() {
  const entityName = "promo code"
  const [tableData, setTableData] = useState([] as Array<PromoCode>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  const loadPage = useCallback((
    pageNo: number = 0,
    pageSize: number = 10,
    sorts: Array<string> = ["createdAt,desc"],
    criteria: any = {}
  ): void => {
    setCurrentPageNo(pageNo)
    PromoCodeService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      code: criteria.code || null,
      batchId: criteria.batchId || null,
      enabled: criteria.enabled || null
    }).then(r => {
      setTableData(r.data.content)
      setTotalCount(r.data.totalElements)
      setTotalPages(r.data.totalPages)
    })
  }, [])

  function generate(formData: GeneratePromoCode): Promise<void> {
    const data = { ...formData }
    if (data.expiry) {
      data.expiry = dayjs(data.expiry).endOf('day').toISOString()
    }
    return PromoCodeService.generate(data).then(() => {
      loadPage(currentPageNo)
    })
  }

  return (
    <RoleAdmin>
      <LayoutBody className="flex flex-col" fixedHeight>
        <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:gap-y-0">
          <PaginatedDataTable
            paginationMode="server"
            columns={tableColumns()}
            data={tableData}
            totalCount={totalCount}
            totalPages={totalPages}
            loadPage={loadPage}
          >
            <DataTableToolbar entityName={entityName} loadPage={loadPage} save={generate} />
          </PaginatedDataTable>
        </div>
      </LayoutBody>
    </RoleAdmin>
  )
}
