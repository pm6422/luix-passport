import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useNavigate } from "react-router-dom"
import { PasswordInput } from "@/components/custom/password-input";
import { AccountService } from "@/services/account-service"
import { type UserRegistrationFormSchema, userRegistrationFormSchema } from "@/domains/user-registration"
import { toast } from "sonner";
import { useState, useMemo } from "react"
import { getErrorMessage } from "@/lib/handle-error"
import { LoadingButton } from "@/components/custom/loading-button"
import InputFormField from "@/components/custom/form-field/input"
import { Link } from "react-router-dom"
import { CheckIcon, XIcon } from "lucide-react"

export function SignUpForm() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [password, setPassword] = useState("")
  const [isPasswordStrongEnough, setIsPasswordStrongEnough] = useState(false)

  const form = useForm<UserRegistrationFormSchema>({
    resolver: zodResolver(userRegistrationFormSchema),
    defaultValues: {
      email: "",
      username: "",
      password: "",
      confirmPassword: "",
    },
  });

  const checkStrength = (pass: string) => {
    const requirements = [
      { regex: /.{8,}/, text: "At least 8 characters" },
      { regex: /[0-9]/, text: "At least 1 number" },
      { regex: /[a-z]/, text: "At least 1 lowercase letter" },
      { regex: /[A-Z]/, text: "At least 1 uppercase letter" },
    ]

    return requirements.map((req) => ({
      met: req.regex.test(pass),
      text: req.text,
    }))
  }

  const strength = useMemo(() => checkStrength(password), [password])

  const strengthScore = useMemo(() => {
    const score = strength.filter((req) => req.met).length
    // 设置密码是否足够强（这里设置为至少满足3个条件）
    setIsPasswordStrongEnough(score >= 3)
    return score
  }, [strength])

  const getStrengthColor = (score: number) => {
    if (score === 0) return "bg-border"
    if (score <= 1) return "bg-red-500"
    if (score <= 2) return "bg-orange-500"
    if (score === 3) return "bg-amber-500"
    return "bg-emerald-500"
  }

  const getStrengthText = (score: number) => {
    if (score === 0) return "Enter a password"
    if (score <= 2) return "Weak password"
    if (score === 3) return "Medium password"
    return "Strong password"
  }

  async function onSubmit(data: UserRegistrationFormSchema) {
    // 检查密码强度
    if (!isPasswordStrongEnough) {
      toast.error("Password does not meet the strength requirements")
      return
    }

    // 检查密码和确认密码是否匹配
    if (data.password !== data.confirmPassword) {
      toast.error("Passwords do not match")
      return
    }

    setIsLoading(true)

    toast.promise(AccountService.register(data), {
      loading: "Registering account...",
      success: () => {
        setIsLoading(false)
        navigate("/sign-in")
        return "Registered account successfully"
      },
      error: (error) => {
        setIsLoading(false)
        return getErrorMessage(error)
      }
    })
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="w-full">
        <div className="flex flex-col w-full h-full px-5 text-center">
          <h3 className="mb-6 text-2xl font-bold">Create Account</h3>

          <div className="space-y-4">
            <InputFormField
              control={form.control}
              name="email"
              label="Email"
              formItemClassName="text-left"
              inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
            />

            <InputFormField
              control={form.control}
              name="username"
              label="Username"
              formItemClassName="text-left"
              inputClassName="w-full h-14 px-5 py-4 rounded-2xl"
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem className="text-left">
                  <FormLabel className="mb-2 text-sm">Password</FormLabel>
                  <FormControl>
                    <PasswordInput
                      className="w-full h-14 px-5 py-4 rounded-2xl"
                      {...field}
                      onChange={(e) => {
                        field.onChange(e);
                        setPassword(e.target.value);
                      }}
                    />
                  </FormControl>

                  {/* Password strength indicator */}
                  <div
                    className="bg-border mt-3 mb-2 h-1 w-full overflow-hidden rounded-full"
                    role="progressbar"
                    aria-valuenow={strengthScore}
                    aria-valuemin={0}
                    aria-valuemax={4}
                    aria-label="Password strength"
                  >
                    <div
                      className={`h-full ${getStrengthColor(strengthScore)} transition-all duration-500 ease-out`}
                      style={{ width: `${(strengthScore / 4) * 100}%` }}
                    ></div>
                  </div>

                  {/* Password strength description */}
                  <p className="text-foreground mb-2 text-sm font-medium">
                    {getStrengthText(strengthScore)}. Must contain:
                  </p>

                  {/* Password requirements list */}
                  <ul className="space-y-1.5" aria-label="Password requirements">
                    {strength.map((req, index) => (
                      <li key={index} className="flex items-center gap-2">
                        {req.met ? (
                          <CheckIcon
                            size={16}
                            className="text-emerald-500"
                            aria-hidden="true"
                          />
                        ) : (
                          <XIcon
                            size={16}
                            className="text-muted-foreground/80"
                            aria-hidden="true"
                          />
                        )}
                        <span
                          className={`text-xs ${req.met ? "text-emerald-600" : "text-muted-foreground"}`}
                        >
                          {req.text}
                          <span className="sr-only">
                            {req.met ? " - Requirement met" : " - Requirement not met"}
                          </span>
                        </span>
                      </li>
                    ))}
                  </ul>

                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="confirmPassword"
              render={({ field }) => (
                <FormItem className="text-left">
                  <FormLabel className="mb-2 text-sm">Confirm Password</FormLabel>
                  <FormControl>
                    <PasswordInput
                      className="w-full h-14 px-5 py-4 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <LoadingButton
            type="submit"
            loading={isLoading}
            className="w-full h-14 px-5 py-4 mt-4 mb-2 text-sm font-medium rounded-2xl"
            disabled={!isPasswordStrongEnough || isLoading}
          >
            {isLoading ? "Waiting" : "Create Account"}
          </LoadingButton>

          <p className="mt-3 text-sm text-muted-foreground">
            Already have an account?{" "}
            <Link to="/sign-in" className="font-bold text-primary hover:underline">Sign In</Link>
          </p>
        </div>
      </form>
    </Form>
  );
}