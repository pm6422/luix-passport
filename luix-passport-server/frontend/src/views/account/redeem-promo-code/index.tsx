import { Separator } from "@/components/ui/separator"
import { RedeemPromoCodeForm } from "./redeem-promo-code-form"

export default function RedeemPromoCode() {
  return (
    <div className="flex flex-col gap-y-6">
      <div>
        <h3 className="text-lg font-medium">Redeem Promo Code</h3>
        <p className="text-sm text-muted-foreground">
          Enter a promo code to upgrade or extend your membership.
        </p>
      </div>
      <Separator />
      <RedeemPromoCodeForm />
    </div>
  )
}
