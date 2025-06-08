"use client"

import { useMemo, useEffect } from "react"
import { IconCheck, IconX } from "@tabler/icons-react"

interface Props {
  password: string
  minLength?: number
  onStrengthChange?: (isValid: boolean) => void
}

export function PasswordStrengthIndicator({
                                   password,
                                   minLength = 5,  // default minimum length is 5
                                   onStrengthChange,
                                 }: Props) {
  // Password requirements
  const requirements = useMemo(() => [
    {
      regex: new RegExp(`.{${minLength},}`),
      text: `At least ${minLength} characters`
    },
    { regex: /[0-9]/, text: "At least 1 number" },
    { regex: /[a-z]/, text: "At least 1 lowercase letter" },
    { regex: /[A-Z]/, text: "At least 1 uppercase letter" },
  ], [minLength])  // 当minLength变化时重新计算

  // Check which requirements are met
  const strength = useMemo(() => (
    requirements.map(req => ({
      met: req.regex.test(password),
      text: req.text,
    }))
  ), [password, requirements])

  // Calculate strength score (0-4)
  const strengthScore = useMemo(() => (
    strength.filter(req => req.met).length
  ), [strength])

  // Password is valid only when all requirements are met
  const isValid = strengthScore === requirements.length

  // Notify parent component about password validity
  useEffect(() => {
    onStrengthChange?.(isValid)
  }, [isValid, onStrengthChange])

  // Progress bar color
  const strengthColor = useMemo(() => {
    if (strengthScore === 0) return "bg-gray-300"
    if (strengthScore <= Math.floor(requirements.length/2)) return "bg-red-500"
    if (strengthScore === requirements.length-1) return "bg-yellow-500"
    return "bg-green-500"
  }, [strengthScore, requirements.length])

  // Strength description text
  const strengthText = useMemo(() => {
    if (strengthScore === 0) return "Enter a password"
    if (strengthScore <= Math.floor(requirements.length/2)) return "Weak password"
    if (strengthScore === requirements.length-1) return "Medium password"
    return "Strong password"
  }, [strengthScore, requirements.length])

  return (
    <div className="mt-2">
      {/* Password strength meter */}
      <div className="bg-gray-200 h-1 w-full rounded-full overflow-hidden">
        <div
          className={`h-full ${strengthColor} transition-all duration-300`}
          style={{ width: `${(strengthScore / requirements.length) * 100}%` }}
        />
      </div>

      {/* Strength description */}
      <p className="text-sm font-medium mt-3 mb-2">
        {strengthText} {strengthScore > 0 && `(${strengthScore}/${requirements.length} requirements met)`}
      </p>

      {/* Requirements list */}
      <ul className="space-y-1.5">
        {strength.map((req, index) => (
          <li key={index} className="flex items-center gap-2">
            {req.met ? (
              <IconCheck size={16} className="text-green-500" />
            ) : (
              <IconX size={16} className="text-gray-400" />
            )}
            <span className={`text-xs ${req.met ? "text-green-600" : "text-gray-500"}`}>
              {req.text}
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}