import { useState, useRef, useCallback } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/custom/button"
import InputFormField from "@/components/custom/form-field/input"
import { PasswordInput } from "@/components/custom/password-input"
import { IconReload, IconSend } from "@tabler/icons-react"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { toast } from "sonner"
import { PinInput, PinInputField } from "@/components/custom/pin-input"
import { RequiredFormLabel } from "@/components/custom/required-form-label"
import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { AccountService } from "@/services/account-service"
import { getErrorMessage } from "@/lib/handle-error"

const formSchema = z.object({
  currentPassword: z.string().trim().min(1, { message: "Required" }),
  newPassword: z.string().trim().min(1, { message: "Required" }),
  email: z.string().trim(),
  verificationCode: z.string().trim().min(1, { message: "Required" })
})

type FormSchema = z.infer<typeof formSchema>

export function ChangePasswordForm() {
  const { authUser } = useStore(authUserStore)
  const [saving, setSaving] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const lastSentTime = useRef<number>(0)

  const form = useForm<FormSchema>({
    resolver: zodResolver(formSchema),
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

  function onSubmit(formData: FormSchema) {
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
            <FormItem className='space-y-1'>
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
            <FormItem className='space-y-1'>
              <RequiredFormLabel required={true}>New Password</RequiredFormLabel>
              <FormControl>
                <PasswordInput {...field} placeholder=""/>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <InputFormField 
          control={form.control} 
          name="email" 
          label="Email"
          value={authUser?.email}
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
              <RequiredFormLabel required={true}>Verification code</RequiredFormLabel>
              <FormControl>
                <PinInput
                  className="flex h-10 space-x-4"
                  value={field.value}
                  onChange={field.onChange}
                  onComplete={(str) => console.log("completed", str)}
                >
                  {Array.from({ length: 6 }, (_, i) => (
                    <PinInputField key={i} component={Input} className="capitalize"/>
                  ))}
                </PinInput>
              </FormControl>
              <FormDescription>Enter the verification code sent to {authUser.email}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <Button type="submit" disabled={saving}>
          {saving ? "Waiting..." : "Update password"}
          {saving && (<IconReload className="ml-1 size-4 animate-spin"/>)}
        </Button>
      </form>
    </Form>
  )
}
