import { IconCheck } from "@tabler/icons-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function PricingPage() {
  const plans = [
    {
      name: "Starter",
      price: "Free",
      description: "Perfect for individual developers",
      features: [
        "Up to 1,000 monthly active users",
        "Basic authentication",
        "Email/password login",
        "Community support",
      ],
      cta: "Get Started",
    },
    {
      name: "Pro",
      price: "$99",
      period: "/month",
      description: "For growing startups and businesses",
      features: [
        "Up to 10,000 monthly active users",
        "Social logins (Google, GitHub)",
        "Multi-factor authentication",
        "Priority support",
        "Basic analytics",
      ],
      cta: "Start Free Trial",
      featured: true,
    },
    {
      name: "Enterprise",
      price: "Custom",
      description: "For large organizations",
      features: [
        "Unlimited users",
        "Advanced security features",
        "SAML/SSO integration",
        "Dedicated account manager",
        "Custom SLAs",
        "On-premise deployment",
      ],
      cta: "Contact Sales",
    },
  ];

  return (
    <div className="container relative grid flex-col justify-center lg:max-w-none lg:grid-cols-3">
      {/* Left side - 1/3 width */}
      <div className="lg:flex relative hidden lg:col-span-1 xl:px-12 lg:px-2 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-4xl text-3xl font-extrabold lg:leading-[50px]">
            Simple, Transparent Pricing
          </h2>
          <h4 className="mt-6 text-lg">
            Choose the plan that fits your needs. Scale as you grow.
          </h4>
          <div className="mt-10">
            <Button
              asChild
              variant="outline"
              className="px-8 py-4 h-14 text-base rounded-2xl"
            >
              <Link to="/features">Compare Features</Link>
            </Button>
          </div>
        </div>
      </div>

      {/* Right side - 2/3 width */}
      <div className="flex flex-col items-center w-full h-full lg:col-span-2 xl:px-32 lg:px-10 px-5 rounded-3xl py-10 overflow-y-auto">
        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">Pricing Plans</h1>
          <p className="text-lg text-muted-foreground mb-10">
            Start for free, upgrade when you're ready
          </p>

          <div className="grid md:grid-cols-3 gap-6">
            {plans.map((plan, index) => (
              <div
                key={index}
                className={`border rounded-2xl p-6 hover:shadow-md transition-shadow ${
                  plan.featured ? "ring-2 ring-primary" : ""
                } flex flex-col`} // Added flex and flex-col
              >
                {plan.featured && (
                  <div className="bg-primary text-primary-foreground text-xs font-medium px-3 py-1 rounded-full inline-block mb-4">
                    Most Popular
                  </div>
                )}
                <h3 className="text-xl font-semibold mb-1">{plan.name}</h3>
                <div className="flex items-baseline mb-3">
                  <span className="text-3xl font-bold">{plan.price}</span>
                  {plan.period && (
                    <span className="text-muted-foreground">{plan.period}</span>
                  )}
                </div>
                <p className="text-muted-foreground mb-6">{plan.description}</p>
                <ul className="space-y-3 mb-8 grow"> {/* Added grow */}
                  {plan.features.map((feature, featureIndex) => (
                    <li key={featureIndex} className="flex items-start">
                      <IconCheck className="h-5 w-5 text-green-500 mr-2 mt-0.5 shrink-0" />
                      <span>{feature}</span>
                    </li>
                  ))}
                </ul>
                <Button
                  asChild
                  className={`w-full py-4 h-14 text-base rounded-2xl ${
                    plan.featured ? "" : "variant-outline"
                  }`}
                >
                  <Link to={plan.name === "Enterprise" ? "/contact" : "/sign-up"}>
                    {plan.cta}
                  </Link>
                </Button>
              </div>
            ))}
          </div>

          <div className="mt-16 border rounded-2xl p-6 bg-muted/50">
            <h3 className="text-xl font-semibold mb-4">
              Frequently Asked Questions
            </h3>
            <div className="space-y-6">
              <div>
                <h4 className="font-medium">Can I change plans later?</h4>
                <p className="text-muted-foreground mt-1">
                  Yes, you can upgrade or downgrade at any time. We'll prorate the
                  difference.
                </p>
              </div>
              <div>
                <h4 className="font-medium">Is there a free trial?</h4>
                <p className="text-muted-foreground mt-1">
                  The Pro plan includes a 14-day free trial. No credit card
                  required.
                </p>
              </div>
              <div>
                <h4 className="font-medium">What payment methods do you accept?</h4>
                <p className="text-muted-foreground mt-1">
                  We accept all major credit cards. Enterprise plans can also pay
                  via invoice.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}