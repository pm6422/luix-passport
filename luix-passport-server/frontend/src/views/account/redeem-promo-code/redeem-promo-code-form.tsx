import { useState } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { LoadingButton } from "@/components/custom/form-element/loading-button"
import InputFormField from "@/components/custom/form-field/input"
import {
  Form,
} from "@/components/ui/form"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { RedeemPromoCode, redeemPromoCodeSchema, initialRedeemPromoCodeState } from "@/domains/promo-code"
import { PromoCodeService } from "@/services/promo-code-service"

export function RedeemPromoCodeForm() {
  const [saving, setSaving] = useState(false)

  const form = useForm<RedeemPromoCode>({
    resolver: zodResolver(redeemPromoCodeSchema),
    defaultValues: initialRedeemPromoCodeState
  })

  function onSubmit(formData: RedeemPromoCode) {
    setSaving(true)
    toast.promise(PromoCodeService.redeem(formData), {
      loading: "Redeeming promo code...",
      success: () => {
        setSaving(false)
        form.reset()
        return "Promo code redeemed successfully"
      },
      error: (error) => {
        setSaving(false)
        return getErrorMessage(error)
      }
    })
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        <InputFormField
          control={form.control}
          name="code"
          label="Promo Code"
          placeholder="Enter your promo code"
          description="Enter the 12-character promo code to redeem your membership."
        />

        <LoadingButton loading={saving} type="submit">
          Redeem Code
        </LoadingButton>
      </form>
    </Form>
  )
}
