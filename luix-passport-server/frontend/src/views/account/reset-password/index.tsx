import { useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useNavigate } from "react-router-dom"
import { LoadingButton } from "@/components/custom/loading-button";
import { Link, useSearchParams } from "react-router-dom"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { toast } from "sonner";
import { Input } from "@/components/ui/input";
import { AccountService } from "@/services/account-service";
import { PasswordInput } from "@/components/custom/password-input";
import { PasswordStrengthIndicator } from "@/components/custom/password-strength-indicator";
import { PASSWORD_MIN_LENGTH } from "@/domains/user-registration";
import { getErrorMessage } from "@/lib/handle-error";

const resetPasswordFormSchema = z
  .object({
    newRawPassword: z
      .string()
      .min(PASSWORD_MIN_LENGTH, { message: "Password must be at least 5 characters" }),
    confirmPassword: z.string(),
    resetCode: z.string().min(1, { message: "Please enter your reset code" }),
  })
  .refine((data) => data.newRawPassword === data.confirmPassword, {
    message: "Passwords don't match.",
    path: ["confirmPassword"],
  });

export type ResetPasswordFormSchema = z.infer<typeof resetPasswordFormSchema>

export default function ForgotPassword() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false);
  const [newRawPassword, setNewRawPassword] = useState("")
  const [isPasswordStrongEnough, setIsPasswordStrongEnough] = useState(false)
  const [searchParams] = useSearchParams();
  const resetCode = searchParams.get("resetCode");

  if (!resetCode) {
    toast.error("Invalid empty reset code");
  }

  const form = useForm<ResetPasswordFormSchema>({
    resolver: zodResolver(resetPasswordFormSchema),
    defaultValues: {
      newRawPassword: "",
      confirmPassword: "",
      resetCode: resetCode || ""
    }
  });

  function completeReset(data: ResetPasswordFormSchema) {
    setIsLoading(true);

    toast.promise(AccountService.completePasswordRecovery(data), {
      loading: "Resetting password...",
      success: () => {
        setIsLoading(false);
        setTimeout(() => {
          navigate("/sign-in")
        }, 2000);
        return "Password reset successfully!";
      },
      error: (error) => {
        setIsLoading(false);
        return getErrorMessage(error);
      }
    });
  }

  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-2">
      {/* Left side - hidden on mobile */}
      <div className="lg:flex relative hidden lg:px-28 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            Reset Your Password
          </h2>
          <h4 className="mt-6 text-lg">
            Secure your account with a new password
          </h4>
          <p className="text-sm mt-10">
            Remember your password?{" "}
            <Link to="/sign-in" className="font-bold text-primary hover:underline">Sign in here</Link>
          </p>
        </div>
      </div>

      {/* Right side - form */}
      <div className="flex flex-col w-full xl:px-48 lg:px-28 px-5 rounded-3xl py-10">
        <img
          src="/assets/images/logos/logo-round.svg"
          alt="Logo"
          className="h-16 my-5 mx-auto"
        />

        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold">Reset Password</h1>
          <p className="text-muted-foreground mt-2">
            Enter your new password below
          </p>
        </div>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(completeReset)} className="space-y-4">
            <FormField
              control={form.control}
              name="resetCode"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Reset Code</FormLabel>
                  <FormControl>
                    <Input
                      disabled
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="newRawPassword"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>New Password</FormLabel>
                  <FormControl>
                    <PasswordInput
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                      onChange={(e) => {
                        field.onChange(e);
                        setNewRawPassword(e.target.value);
                      }}
                    />
                  </FormControl>
                  <PasswordStrengthIndicator
                    password={newRawPassword}
                    onStrengthChange={setIsPasswordStrongEnough}
                    minLength={PASSWORD_MIN_LENGTH}
                  />
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="confirmPassword"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Confirm Password</FormLabel>
                  <FormControl>
                    <PasswordInput
                      className="px-5 py-4 h-14 rounded-2xl"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <LoadingButton
              type="submit"
              className="w-full py-4 h-14 rounded-2xl mt-2"
              loading={isLoading}
              disabled={!isPasswordStrongEnough || isLoading}
            >
              {isLoading ? "Resetting..." : "Reset Password"}
            </LoadingButton>
          </form>
        </Form>

        <p className="mt-6 text-center text-sm text-muted-foreground">
          Need help?{" "}
          <Link to="/contact-us" className="font-bold text-primary hover:underline">
            Contact support
          </Link>
        </p>
      </div>
    </div>
  );
}