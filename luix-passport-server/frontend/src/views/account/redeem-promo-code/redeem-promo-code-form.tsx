import { useState } from "react"
import dayjs from "dayjs"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { LoadingButton } from "@/components/custom/form-element/loading-button"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
  InputOTPSeparator,
} from "@/components/ui/input-otp"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { RedeemPromoCode, redeemPromoCodeSchema, initialRedeemPromoCodeState } from "@/domains/promo-code"
import { PromoCodeService } from "@/services/promo-code-service"
import { useLoginUserStore } from "@/stores/login-user-store"

export function RedeemPromoCodeForm() {
  const [saving, setSaving] = useState(false)
  const { loginUser } = useLoginUserStore()

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
        {loginUser.membershipType && (
          <div className="rounded-lg border bg-card text-card-foreground shadow-sm p-6">
            <h3 className="text-lg font-semibold leading-none tracking-tight mb-2">Current Membership</h3>
            <p className="text-sm text-muted-foreground">
              Type: <span className="font-medium text-foreground">{loginUser.membershipType}</span>
            </p>
            {loginUser.membershipExpiresAt && (
              <p className="text-sm text-muted-foreground mt-1">
                Expires At: <span className="font-medium text-foreground">{dayjs(loginUser.membershipExpiresAt).format("YYYY-MM-DD")}</span>
              </p>
            )}
          </div>
        )}
        <FormField
          control={form.control}
          name="code"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Promo Code</FormLabel>
              <FormControl>
                <InputOTP
                  maxLength={12}
                  {...field}
                  onChange={(value) => field.onChange(value.toUpperCase())}
                >
                  <InputOTPGroup>
                    <InputOTPSlot index={0} />
                    <InputOTPSlot index={1} />
                    <InputOTPSlot index={2} />
                    <InputOTPSlot index={3} />
                  </InputOTPGroup>
                  <InputOTPSeparator />
                  <InputOTPGroup>
                    <InputOTPSlot index={4} />
                    <InputOTPSlot index={5} />
                    <InputOTPSlot index={6} />
                    <InputOTPSlot index={7} />
                  </InputOTPGroup>
                  <InputOTPSeparator />
                  <InputOTPGroup>
                    <InputOTPSlot index={8} />
                    <InputOTPSlot index={9} />
                    <InputOTPSlot index={10} />
                    <InputOTPSlot index={11} />
                  </InputOTPGroup>
                </InputOTP>
              </FormControl>
              <FormDescription>
                Enter the 12-character promo code to redeem your membership.
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <LoadingButton loading={saving} type="submit">
          Redeem Code
        </LoadingButton>
      </form>
    </Form>
  )
}
