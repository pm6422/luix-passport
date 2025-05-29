import { useState, useEffect, useRef } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/custom/button"
import { IconReload, IconSend } from "@tabler/icons-react"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
import InputFormField from "@/components/custom/form-field/input"
import { Input } from "@/components/ui/input"
import { toast } from "sonner"
import { PinInput, PinInputField } from "@/components/custom/pin-input"
import { RequiredFormLabel } from "@/components/custom/required-form-label"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { AccountService } from "@/services/account-service"
import { getErrorMessage } from "@/lib/handle-error"
import { Link } from "react-router-dom"
import { useNavigate } from "react-router-dom"

const formSchema = z.object({
  currentEmail: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  newEmail: z.string().trim().min(1, { message: "Required" }).email("Invalid email format"),
  verificationCode: z.string().trim().min(1, { message: "Required" })
})

type FormSchema = z.infer<typeof formSchema>

export function ChangeEmailForm() {
  const navigate = useNavigate()
  const { loginUser } = useStore(loginUserStore)
  const [verificationCodeInputDisabled, setVerificationCodeInputDisabled] = useState(true)
  const [saving, setSaving] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const lastSentTime = useRef<number>(0)

  const form = useForm<FormSchema>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      currentEmail: loginUser.email,
      newEmail: "",
      verificationCode: ""
    },
    mode: "onChange"
  })

  useEffect(() => {
    AccountService.getCurrentAccount().then(user => {
      form.reset({ currentEmail: user.email })
    })
  }, [form])

 function sendVerificationCode(email: string): void {
   const now = Date.now()
   // If the time since the last send is less than 60 seconds, and the countdown has not ended.
   if (now - lastSentTime.current < 60000 && countdown > 0) {
     toast.error("You can only send a verification code once every one minute.")
     return
   }

    setSaving(true)
   lastSentTime.current = now

    toast.promise(AccountService.sendEmailChangeVerificationCode(email), {
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
        setVerificationCodeInputDisabled(false)
        return "Sent verification code"
      },
      error: (error) => {
        setSaving(false)
        return getErrorMessage(error)
      }
    })
  }

  function onSubmit(formData: FormSchema) {
    setSaving(true)
    toast.promise(AccountService.changeEmail(formData.verificationCode), {
      loading: "Updating account...",
      success: () => {
        setSaving(false)
        navigate("/account-settings/account")
        return "Updated account"
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
        <div>
          <h3 className="text-sm font-medium">Current Email</h3>
          <p className="text-sm text-muted-foreground mt-2">
            {form.getValues("currentEmail")}
          </p>
        </div>

        <InputFormField 
          control={form.control} 
          name="newEmail" 
          label="New Email" 
          required
          formItemClassName="mt-2"
          description="Click right button to send a verification code to your new email."
          icon={
            <Button 
              type="button"
              variant="outline" 
              disabled={form.formState.errors.newEmail != null || saving || !form.getValues("newEmail") || form.getValues("currentEmail") == form.getValues("newEmail")}
              onClick={() => sendVerificationCode(form.getValues("newEmail"))}>
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
                <PinInput
                  className="flex h-10 space-x-4"
                  value={field.value}
                  onChange={field.onChange}
                  disabled={verificationCodeInputDisabled}
                  onComplete={(str) => console.log("completed", str)}
                >
                  {Array.from({ length: 6 }, (_, i) => (
                    <PinInputField key={i} component={Input} className="capitalize"/>
                  ))}
                </PinInput>
              </FormControl>
              <FormDescription className={verificationCodeInputDisabled ? "invisible" : ""}>Enter the verification code sent to {form.getValues("newEmail")}</FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex flex-col sm:flex-row gap-2 sm:justify-end">
          <Link to="/account-settings/account" className="w-full sm:w-auto">
            <Button
              type="button"
              variant="outline"
              className="w-full sm:w-auto"
            >
              Back
            </Button>
          </Link>
          <Button
            type="submit"
            disabled={Object.values(form.formState.errors).length > 0 || saving}
            className="w-full sm:w-auto"
          >
            {saving ? "Waiting..." : "Save"}
            {saving && (<IconReload className="ml-1 size-4 animate-spin"/>)}
          </Button>
        </div>
      </form>
    </Form>
  )
}
