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
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { IconCrown, IconCalendar, IconStar, IconDiamond } from "@tabler/icons-react"
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
          <Card className="bg-primary/5 border-primary/20 shadow-none">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium flex items-center gap-2">
                <IconCrown className="size-4 text-primary" />
                Current Membership
              </CardTitle>
            </CardHeader>
            <CardContent className="grid gap-4 sm:grid-cols-2">
              <div className="flex flex-col gap-1">
                <span className="text-xs text-muted-foreground">Plan Type</span>
                <div>
                  <Badge variant={loginUser.membershipType.includes("PREMIUM") ? "default" : "secondary"}>
                    {loginUser.membershipType.includes("PREMIUM") ? (
                      <IconDiamond className="size-3" />
                    ) : (
                      <IconStar className="size-3" />
                    )}
                    {loginUser.membershipType.replace("ROLE_MEMBER_", "")}
                  </Badge>
                </div>
              </div>
              {loginUser.membershipExpiresAt && (
                <div className="flex flex-col gap-1">
                  <span className="text-xs text-muted-foreground">Expires At</span>
                  <div className="flex items-center gap-2 text-sm font-medium">
                    <IconCalendar className="size-4 text-muted-foreground" />
                    {dayjs(loginUser.membershipExpiresAt).format("YYYY-MM-DD")}
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
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
