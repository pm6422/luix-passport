import { useState, useRef, useCallback } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/ui/button"
import { LoadingButton } from "@/components/custom/loading-button"
import InputFormField from "@/components/custom/form-field/input"
import { PasswordInput } from "@/components/custom/password-input"
import { PasswordStrengthIndicator } from "@/components/custom/password-strength-indicator"
import { IconReload, IconSend } from "@tabler/icons-react"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
import { toast } from "sonner"
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
} from "@/components/ui/input-otp"
import { RequiredFormLabel } from "@/components/custom/required-form-label"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { AccountService } from "@/services/account-service"
import { getErrorMessage } from "@/lib/handle-error"
import { PASSWORD_MIN_LENGTH } from "@/domains/user-registration"

const changePasswordFormSchema = z.object({
  currentPassword: z.string().trim().min(1, { message: "Required" }),
  newPassword: z
    .string()
    .min(PASSWORD_MIN_LENGTH, "New password must be at least 5 characters"),
  email: z.string().trim(),
  verificationCode: z.string().trim().min(1, { message: "Required" })
})

export type ChangePasswordFormSchema = z.infer<typeof changePasswordFormSchema>

export function ChangePasswordForm() {
  const { loginUser } = useStore(loginUserStore)
  const [saving, setSaving] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const lastSentTime = useRef<number>(0)
  const [newPassword, setNewPassword] = useState("")
  const [isPasswordStrongEnough, setIsPasswordStrongEnough] = useState(false)

  const form = useForm<ChangePasswordFormSchema>({
    resolver: zodResolver(changePasswordFormSchema),
    defaultValues: {
      currentPassword: "",
      newPassword: "",
      email: "",
      verificationCode: ""
    }
  })

  const sendVerificationCode = useCallback(() => {
    const now = Date.now()
    // If the time since the last send is less than 60 seconds, and the countdown has not ended.
    if (now - lastSentTime.current < 60000 && countdown > 0) {
      toast.error("You can only send a verification code once every one minute.")
      return
    }

    setSaving(true)
    lastSentTime.current = now

    toast.promise(AccountService.sendPasswordChangeVerificationCode(), {
      loading: "Sending verification code...",
      success: () => {
        setSaving(false)
        setCountdown(60)
        const timer = setInterval(() => {
          setCountdown((prev) => {
            if (prev <= 1) {
              clearInterval(timer)
              return 0
            }
            return prev - 1
          })
        }, 1000)
        return "Sent verification code"
      },
      error: (error) => {
        setSaving(false)
        return getErrorMessage(error)
      }
    })
  }, [countdown])

  function onSubmit(formData: ChangePasswordFormSchema) {
    setSaving(true)
    toast.promise(AccountService.updatePassword(formData), {
      loading: "Updating password...",
      success: () => {
        setSaving(false)
        return "Updated password"
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
        <FormField
          control={form.control}
          name="currentPassword"
          render={({ field }) => (
            <FormItem className="space-y-1">
              <RequiredFormLabel required={true}>Current Password</RequiredFormLabel>
              <FormControl>
                <PasswordInput {...field} placeholder=""/>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="newPassword"
          render={({ field }) => (
            <FormItem className="space-y-1">
              <RequiredFormLabel required={true}>New Password</RequiredFormLabel>
              <FormControl>
                <PasswordInput
                  {...field}
                  placeholder=""
                  onChange={(e) => {
                    field.onChange(e);
                    setNewPassword(e.target.value);
                  }}
                />
              </FormControl>
              <PasswordStrengthIndicator
                password={newPassword}
                onStrengthChange={setIsPasswordStrongEnough}
                minLength={PASSWORD_MIN_LENGTH}
              />
              <FormMessage />
            </FormItem>
          )}
        />
        <InputFormField 
          control={form.control} 
          name="email" 
          label="Email"
          value={loginUser?.email}
          disabled
          description="Click right button to send a verification code to your email."
          icon={
            <Button 
              type="button"
              variant="outline"
              onClick={() => sendVerificationCode()}>
                <IconSend className="size-4 mr-1" />
                {saving ? "Sending..." : "Send"}
                {saving && (<IconReload className="ml-1 size-4 animate-spin"/>)}
            </Button>
          }
        />
        <FormField
          control={form.control}
          name="verificationCode"
          render={({ field }) => (
            <FormItem>
              <RequiredFormLabel required={true}>Verification Code</RequiredFormLabel>
              <FormControl>
                <InputOTP
                  maxLength={6}
                  value={field.value}
                  onChange={field.onChange}
                >
                  <InputOTPGroup>
                    {Array.from({ length: 6 }, (_, i) => (
                      <InputOTPSlot index={i} className="capitalize"/>
                    ))}
                  </InputOTPGroup>
                </InputOTP>
              </FormControl>
              <FormDescription>Enter the verification code sent to {loginUser.email}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end">
          <LoadingButton
            type="submit"
            loading={saving}
            disabled={!isPasswordStrongEnough || saving}
            className="w-full sm:w-auto"
          >
            {saving ? "Waiting" : "Update password"}
          </LoadingButton>
        </div>
      </form>
    </Form>
  )
}
