import { useState, useCallback } from "react"
import dayjs from "dayjs"
import { LayoutBody } from "@/layouts/layout-definitions"
import { PaginatedDataTable } from "@/components/custom/data-table/paginated-data-table"
import { getPromoCodeColumns } from "./table/promo-code-table-columns"
import { PromoCode, GeneratePromoCode } from "@/domains/promo-code"
import { PromoCodeService } from "@/services/promo-code-service"
import { RoleAdmin } from "@/layouts/permission/role-control"
import { DataTableToolbar } from "./table/promo-code-table-toolbar"
import { toast } from "sonner"
import { z } from "zod"
import { Input } from "@/components/ui/input"
import { FormField, FormItem, FormLabel, FormControl, FormMessage } from "@/components/ui/form"
import { FormDialog } from "@/components/custom/dialog/form-dialog"

const emailSchema = z.object({
  email: z.string().email("Invalid email address"),
})

export default function PromoCodes() {
  const entityName = "promo code"
  const [tableData, setTableData] = useState([] as Array<PromoCode>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)

  const [emailDialogOpen, setEmailDialogOpen] = useState(false)
  const [selectedPromoCode, setSelectedPromoCode] = useState<PromoCode | null>(null)

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

  function toggleEnabled(id: string, enabled: boolean) {
    PromoCodeService.updateEnabled(id, enabled).then(() => {
      toast.success(`Promo code ${enabled ? 'enabled' : 'disabled'}`)
      loadPage(currentPageNo) // Refresh the table
    })
  }

  function openEmailDialog(promoCode: PromoCode) {
    setSelectedPromoCode(promoCode)
    setEmailDialogOpen(true)
  }

  return (
    <RoleAdmin>
      <LayoutBody className="flex flex-col" fixedHeight>
        <div className="-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:gap-y-0">
          <PaginatedDataTable
            paginationMode="server"
            columns={getPromoCodeColumns({ toggleEnabled: toggleEnabled, onSendEmail: openEmailDialog })}
            data={tableData}
            totalCount={totalCount}
            totalPages={totalPages}
            loadPage={loadPage}
          >
            <DataTableToolbar entityName={entityName} loadPage={loadPage} save={generate} />
          </PaginatedDataTable>
        </div>

        <FormDialog
          size="sm"
          entityName="Invitation"
          open={emailDialogOpen}
          onOpenChange={setEmailDialogOpen}
          schema={emailSchema}
          defaultValues={{ email: "" }}
          showFormErrors={false}
          save={async (formData) => {
            if (selectedPromoCode) {
              await PromoCodeService.sendEmail(selectedPromoCode.id, formData.email)
            }
          }}
          loadingMessage="Sending email..."
          successMessage="Email sent successfully"
          saveButtonText="Send Email"
          renderFields={(form) => (
            <>
              <div className="text-sm text-muted-foreground mb-4">
                Send promo code <strong>{selectedPromoCode?.code}</strong> to a user via email.
              </div>
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input placeholder="user@example.com" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </>
          )}
        >
          {/* This child is needed as trigger but we control open state externally from the table action */}
          <div />
        </FormDialog>
      </LayoutBody>
    </RoleAdmin>
  )
}
